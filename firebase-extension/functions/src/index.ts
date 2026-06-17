import {onRequest} from "firebase-functions/v2/https";
import * as logger from "firebase-functions/logger";
import {initializeApp, cert} from "firebase-admin/app";
import {getAuth} from "firebase-admin/auth";
import * as path from "path";
import * as fs from "fs";

/**
 * Initialize Firebase Admin SDK with local service account support.
 */
const initializeFirebase = () => {
  if (!process.env.FUNCTIONS_EMULATOR) {
    initializeApp();
    return;
  }

  // Try to find service account key from different possible locations
  const possiblePaths = [
    process.env.SERVICE_ACCOUNT_KEY_PATH, // From .env.local
    path.join(process.cwd(), "service-account.json"),
    path.join(__dirname, "..", "service-account.json")
  ].filter((p) => p !== undefined) as string[];

  const saPath = possiblePaths.find((p) => fs.existsSync(p));

  if (saPath) {
    initializeApp({credential: cert(saPath)});
    logger.info(`Initialized with local service account at: ${saPath}`);
  } else {
    logger.warn("Running in emulator but no service-account.json found. Custom token generation might fail.");
    initializeApp();
  }
};

initializeFirebase();

/**
 * Generic custom service login verification.
 */
export const signInWithCustomService = onRequest({cors: true}, async (req, res) => {
  const {access_token, provider} = req.body;

  if (!access_token || !provider) {
    logger.error("Missing access_token or provider");
    res.status(400).json({error: "Missing access_token or provider"});
    return;
  }

  try {
    const providersConfigJson = process.env.AUTH_PROVIDERS_JSON;
    if (!providersConfigJson) {
      logger.error("AUTH_PROVIDERS_JSON environment variable is not set.");
      res.status(500).json({error: "Server configuration error."});
      return;
    }

    const SUPPORTED_PROVIDERS = JSON.parse(providersConfigJson);
    const providerTag = provider.toLowerCase();
    const config = SUPPORTED_PROVIDERS[providerTag];

    if (!config || !config.verify_url) {
      logger.error(`Provider ${provider} is not configured.`);
      res.status(400).json({error: `Provider ${provider} is not configured.`});
      return;
    }

    const verifyResponse = await fetch(config.verify_url, {
      headers: {"Authorization": `Bearer ${access_token}`},
    });

    if (!verifyResponse.ok) {
      const errorText = await verifyResponse.text();
      logger.error(`${provider} verification failed`, errorText);
      res.status(401).json({error: `Invalid ${provider} token`});
      return;
    }

    const thirdPartyUser = await verifyResponse.json();
    const thirdPartyId = thirdPartyUser.id;
    const uid = `custom:${providerTag}:${thirdPartyId}`;

    // Extract user info
    const email = thirdPartyUser.email || null;
    const displayName = thirdPartyUser.name || thirdPartyUser.username || thirdPartyUser.login;
    const photoURL = thirdPartyUser.profile_image?.large || thirdPartyUser.avatar_url || null;

    // 1. Create or Update user record in Firebase Auth to ensure info shows in Console
    try {
      await getAuth().getUser(uid);
      // User exists, update info if needed
      await getAuth().updateUser(uid, {
        email: email || undefined,
        displayName: displayName || undefined,
        photoURL: photoURL || undefined
      });
    } catch (error: any) {
      if (error.code === "auth/user-not-found") {
        // User doesn't exist, create it
        await getAuth().createUser({
          uid: uid,
          email: email || undefined,
          displayName: displayName || undefined,
          photoURL: photoURL || undefined
        });
        logger.info(`Created new Firebase user record for ${uid}`);
      } else {
        throw error;
      }
    }

    // 2. Generate Custom Token with additional claims
    const additionalClaims = {
      provider: providerTag,
      username: thirdPartyUser.username || thirdPartyUser.login,
    };

    const customToken = await getAuth().createCustomToken(uid, additionalClaims);

    logger.info(`Successfully generated Custom Token for user ${uid}`);
    res.json({custom_token: customToken});
  } catch (error) {
    logger.error("Internal Server Error", error);
    res.status(500).json({error: "Internal Server Error"});
  }
});

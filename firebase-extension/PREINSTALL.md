# OmniFeed Custom Auth Service

This extension provides a secure way to integrate third-party authentication services (like Unsplash) into your Firebase project by exchanging their access tokens for Firebase Custom Tokens.

## Features
- **Multi-provider support**: Configure multiple third-party services using a simple JSON configuration.
- **Secure token exchange**: Verifies third-party tokens on the server-side and generates standard Firebase Custom Tokens.
- **Seamless SDK integration**: Works out-of-the-box with the OmniFeed KMP Auth SDK.

## Prerequisites
Before installing this extension, ensure you have:
1.  **Firebase Authentication** enabled in your project.
2.  **Cloud Functions** enabled (requires the Blaze plan).
3.  **Third-party API credentials**: You must have a Client ID and Client Secret from the provider you intend to use (e.g., Unsplash Developer Portal).

## Configuration Parameters
During installation, you will need to provide:
-   **Cloud Functions location**: Choose the region closest to your users (e.g., `asia-east1` for Taiwan).
-   **Auth Providers JSON**: A JSON object mapping provider tags to their verification URLs.
    -   Example: `{"unsplash": {"verify_url": "https://api.unsplash.com/me"}}`

## Billing
This extension uses the following Firebase services which may incur charges:
-   Cloud Functions (Node.js 22 runtime)

For more information, see the [OmniFeed KMP Documentation](https://github.com/lackary/omnifeed-kmp).

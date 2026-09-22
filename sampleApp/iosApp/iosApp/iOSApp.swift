import SwiftUI
import OSLog
import FirebaseCore
import GoogleSignIn
import Shared

fileprivate let logger = Logger(
    subsystem: "io.lackstudio.omnifeed.app",
    category: "General"
)

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {
        if let filePath = Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist"),
           FileManager.default.fileExists(atPath: filePath) {
            FirebaseApp.configure()
            if let webClientID = FirebaseApp.app()?.options.clientID {
                logger.debug("webClientID: \(webClientID)")
                AppInitializer.shared.onApplicationStart(serverId: webClientID)
            } else {
                AppInitializer.shared.onApplicationStart(serverId: nil)
            }
        } else {
            logger.warning("GoogleService-Info.plist missing, using default serverId from BuildKonfig")
            AppInitializer.shared.onApplicationStart(serverId: nil)
        }
        
        return true
    }
    
    func application(
        _ app: UIApplication,
        open url: URL,
        options: [UIApplication.OpenURLOptionsKey : Any] = [:]
    ) -> Bool {
        let handled = GIDSignIn.sharedInstance.handle(url)
        if handled {
            return true
        }

        return false
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate

    init() {
        logger.info("iOSApp.swift")
        logger.debug("iOSApp.swift")
    }

    var body: some Scene {
        WindowGroup {
            ContentView().onOpenURL(perform: { url in
                GIDSignIn.sharedInstance.handle(url)
            })
        }
    }
}

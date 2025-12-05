import SwiftUI
import OSLog
import FirebaseCore
import GoogleSignIn
import ComposeApp

fileprivate let logger = Logger(
    subsystem: "io.lackstudio.omnifeed.app",
    category: "General")

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {
        FirebaseApp.configure()
        guard let webClientID: String = FirebaseApp.app()?.options.clientID else {
            // Handle error: Incorrect filename or file not added to the Target
            fatalError("Unable to read CLIENT_ID from GoogleService-Info.plist")
        }
        logger.info("webClientID: \(webClientID)")
        AppInitializer.shared.onApplicationStart(webClientId: webClientID)
        
        return true
    }
    
    func application(
        _ app: UIApplication,
        open url: URL,
        options: [UIApplication.OpenURLOptionsKey : Any] = [:]
    ) -> Bool {
        
        var handled = GIDSignIn.sharedInstance.handle(url)
        if handled {
            return true
        }

        return false
    }
}

@main
struct iOSApp: App {
    init() {
        logger.info("iOSApp.swift")
        logger.debug("iOSApp.swift")
        @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    }
    var body: some Scene {
        WindowGroup {
            ContentView().onOpenURL(perform: { url in
                GIDSignIn.sharedInstance.handle(url)
            })
        }
    }
}

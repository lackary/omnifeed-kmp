# See it in action

You have successfully installed the **OmniFeed Custom Auth Service** extension!

## Next steps

### 1. Secure your Auth Providers JSON
If you haven't already, make sure the **Auth Providers JSON** parameter is correctly configured in the Firebase Console with the appropriate verification URLs for your third-party services.

### 2. Integration with OmniFeed KMP SDK
To use this extension in your Kotlin Multiplatform application:
- Configure your `OmniFeedConfig` with the function URL provided by this extension.
- Use `signInWithCustomService` in the `AuthRepository`.

### 3. Monitoring
You can monitor the execution of the token exchange function in the [Firebase Console Logs](https://console.firebase.google.com/project/${param:PROJECT_ID}/functions/logs?search=ext-${param:EXT_INSTANCE_ID}).

## Support
If you encounter any issues, please check the [OmniFeed GitHub Repository](https://github.com/lackary/omnifeed-kmp) or reach out to the maintainers.

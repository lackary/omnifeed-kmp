#!/bin/bash
# [LEGACY] This script was used for CocoaPods integration before migrating to SPM.
# Preserved for historical reference only. Do not execute in current SPM setup.

set -e
cd "$(dirname "$0")/.."

# --- Step 1: Clean old build caches ---
echo "🧹 [1/4] Cleaning previous builds..."
./gradlew clean
rm -rf sampleApp/iosApp/Pods
rm -f sampleApp/iosApp/Podfile.lock
rm -rf ~/Library/Developer/Xcode/DerivedData/OmniFeed-*

# --- Step 2: Create an empty resource directory ---
echo "📁 [2/4] Creating dummy resource directory for CocoaPods..."
mkdir -p sampleApp/shared/build/compose/cocoapods/compose-resources

# --- Step 3: Generate a dummy Framework ---
echo "⚙️ [3/4] Generating dummy framework for CocoaPods..."
./gradlew :sampleApp:shared:generateDummyFramework

# --- Step 4: Run Pod Install ---
echo "📦 [4/4] Installing Pods..."
cd sampleApp/iosApp
pod install --repo-update --clean-install

echo "✅ Setup complete! You can now open 'iosApp.xcworkspace' in Xcode and run the project."

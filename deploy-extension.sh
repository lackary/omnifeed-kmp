#!/bin/bash

# Navigate to the project root
cd "$(dirname "$0")"

# --- 1. Defaults (can be overridden by environment variables) ---
# NOTE: If this is your FIRST time publishing as this PUBLISHER_ID,
# you must first register as a publisher by running:
#   firebase ext:dev:register-publisher

# Common Firebase Extension CLI commands for reference:
#   List:       firebase ext:dev:list $PUBLISHER_ID
#   Upload:     firebase ext:dev:upload $PUBLISHER_ID/$EXTENSION_ID --local --root ./firebase-extension
#   Deprecate:  firebase ext:dev:deprecate $PUBLISHER_ID/$EXTENSION_ID <version-range>

PUBLISHER_ID="${PUBLISHER_ID:-lackstudio}"
EXTENSION_DIR="${EXTENSION_DIR:-./firebase-extension}"

# --- 2. Automatically extract info from extension.yaml ---
if [ -f "$EXTENSION_DIR/extension.yaml" ]; then
    # Extract extension ID (name field)
    EXTENSION_ID=$(grep "^name:" "$EXTENSION_DIR/extension.yaml" | head -n 1 | awk '{print $2}')
    # Extract current version
    CURRENT_VER=$(grep "^version:" "$EXTENSION_DIR/extension.yaml" | head -n 1 | awk '{print $2}')
else
    echo "❌ Error: extension.yaml not found in $EXTENSION_DIR."
    echo "💡 Sample Usage:"
    echo "  ./deploy-extension.sh          (Upload using defaults)"
    echo "  ./deploy-extension.sh alpha    (Upload as an alpha stage)"
    exit 1
fi

EXTENSION_REF="$PUBLISHER_ID/$EXTENSION_ID"

echo "🚀 Starting Firebase Extension upload to Extensions Hub..."
echo "📦 Extension: $EXTENSION_REF"
echo "ℹ️  Version:   $CURRENT_VER"

# --- 3. Alpha/Stage Handling ---
# You can pass stage as an argument.
# Samples:
#   ./deploy-extension.sh alpha
#   ./deploy-extension.sh beta
#   ./deploy-extension.sh rc
STAGE=$1
UPLOAD_OPTS="--local --root $EXTENSION_DIR"

if [ ! -z "$STAGE" ]; then
    echo "🛠️  Uploading as stage: $STAGE"
    UPLOAD_OPTS="$UPLOAD_OPTS --stage $STAGE"
fi

echo "⚠️  Note: Upload will fail if this version has already been uploaded."
echo "--------------------------------------------------"

# Execute the upload
firebase ext:dev:upload "$EXTENSION_REF" $UPLOAD_OPTS

if [ $? -eq 0 ]; then
    echo "--------------------------------------------------"
    echo "✅ Extension uploaded successfully!"
    echo "🔗 View it in the publisher console:"
    echo "https://console.firebase.google.com/u/0/project/_/extension-publisher/extensions/$EXTENSION_ID"
else
    echo "--------------------------------------------------"
    echo "❌ Upload failed. Please check the logs above."
    exit 1
fi

#!/bin/bash

# Navigate to the project root
cd "$(dirname "$0")" || exit

echo "🛠️  OmniFeed Extension Management Tool"

COMMAND=$1
ARG2=$2
ARG3=$3
ARG4=$4

# Function to show usage
show_usage() {
    echo "💡 Usage:"
    echo "  ./manage-extensions.sh list"
    echo "  ./manage-extensions.sh install <instance-id> <source> [project-id]"
    echo "  ./manage-extensions.sh update  <instance-id> <source> [project-id]"
    echo "  ./manage-extensions.sh deploy  [project-id]"
    echo ""
    echo "Example (Local): ./manage-extensions.sh update omnifeed-auth-custom-service ./firebase-extension lackstudio-omnifeed-dev"
}

# 1. Check for installed extensions
if [ "$COMMAND" == "list" ]; then
    firebase ext:list
    exit 0
fi

# 2. Direct Deploy command
if [ "$COMMAND" == "deploy" ]; then
    PID=${ARG2:-$PROJECT_ID}
    PROJECT_FLAG=""
    if [ ! -z "$PID" ]; then PROJECT_FLAG="--project $PID"; fi
    echo "🚀 Deploying all extensions to Firebase Console..."
    firebase deploy --only extensions $PROJECT_FLAG
    exit 0
fi

# 3. Validation for Install/Update
if [[ -z "$COMMAND" || -z "$ARG2" || -z "$ARG3" ]]; then
    show_usage
    exit 1
fi

INSTANCE_ID=$ARG2
SOURCE=$ARG3
PROJECT_ID=$ARG4

# Construct project flag if provided
PROJECT_FLAG=""
if [ ! -z "$PROJECT_ID" ]; then
    PROJECT_FLAG="--project $PROJECT_ID"
fi

if [ "$COMMAND" == "install" ]; then
    echo "📥 Installing extension '$INSTANCE_ID' from '$SOURCE'..."
    firebase ext:install "$SOURCE" --local --project "$PROJECT_ID"
    echo "✅ Extension added to firebase.json."
    echo "💡 Run './manage-extensions.sh deploy' to push it to the cloud."
    exit 0
fi

if [ "$COMMAND" == "update" ]; then
    echo "🔄 Step 1: Updating local manifest for $INSTANCE_ID..."
    # Try update, but don't fail if it's a local source
    firebase ext:update "$INSTANCE_ID" "$SOURCE" $PROJECT_FLAG

    echo "🚀 Step 2: Deploying changes to Firebase Console..."
    firebase deploy --only extensions $PROJECT_FLAG

    if [ $? -eq 0 ]; then
        echo "✅ Extension $INSTANCE_ID is now live!"
    else
        echo "❌ Deployment failed."
    fi
    exit 0
fi

# If we get here, command was unknown
echo "❌ Unknown command: $COMMAND"
show_usage
exit 1

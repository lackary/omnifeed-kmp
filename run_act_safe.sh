#!/bin/bash

# ==============================================================================
# OmniFeed-KMP Local CI Runner (Self-Hosted Mode)
# ==============================================================================

# Security Check
if [ ! -f "gradlew" ] || [ ! -d ".github" ]; then
    echo "❌ Error: Please run this script from the project root."
    exit 1
fi

# Configure Artifact Paths
ARTIFACT_PATH="./build/act-artifacts"
CACHE_PATH="./build/act-cache"
# Note: In self-hosted mode, XDG_CACHE_HOME may affect other tools on your local machine.
# It is recommended to override this only during act execution, or remove this line if necessary.
export XDG_CACHE_HOME="$(pwd)/build/act-xdg-cache"

mkdir -p "$ARTIFACT_PATH" "$CACHE_PATH" "$XDG_CACHE_HOME"

# Retrieve Token
if ! command -v gh &> /dev/null; then
    echo "⚠️  GitHub CLI (gh) not detected."
    TOKEN_ARG=""
    EXPORT_TOKEN=""
else
    RAW_TOKEN=$(gh auth token)
    TOKEN_ARG="-s GITHUB_TOKEN=$RAW_TOKEN"
    EXPORT_TOKEN=$RAW_TOKEN
    echo "✅ GitHub Token auto-detected."
fi

# Secrets
SECRETS_FILE=".secrets"
if [ ! -f "$SECRETS_FILE" ]; then
    echo "📝 Creating template $SECRETS_FILE..."
    cat <<EOF > "$SECRETS_FILE"
UNSPLASH_ACCESS_KEY=your_key
UNSPLASH_SECRET_KEY=your_key
GOOGLE_SERVICES_WEB_CLIENT_ID=your_key
GOOGLE_CLIENT_ID=your_key
GOOGLE_REVERSED_CLIENT_ID=your_key
EOF
    echo "⚠️  Please fill in real keys in $SECRETS_FILE."
    exit 1
fi

# ==============================================================================
# Menu
# ==============================================================================
echo ""
echo "Select Workflow:"
echo "  1) Build & Test (CI)"
echo "     - Uses your local Mac environment (No Docker)"
echo "     - Can run iOS tests if you remove 'ACT=true' (Fast & Real)"
echo ""
echo "  2) Release Workflow"
echo "     - Simulates release on local Mac"
echo ""
echo "  3) Release Logic Check (Host Mode)"
echo "     - Runs semantic-release directly (Fastest)"
echo ""
read -p "Enter option [1, 2, or 3] (Default 1): " choice
choice=${choice:-1}

# Hybrid Mode Configuration:
# 1. macOS (Build & Test) -> Use local machine (-self-hosted) to resolve iOS compilation issues.
# 2. Ubuntu (Release) -> Use Docker (full-22.04) to resolve Node.js environment issues and keep it clean.
# 3. Restore --container-architecture to support Docker.
ACT_COMMON_ARGS="--platform macos-latest=-self-hosted \
--platform ubuntu-latest=catthehacker/ubuntu:full-22.04 \
--container-architecture linux/amd64 \
--env ACT=true \
--secret-file $SECRETS_FILE \
--artifact-server-path $ARTIFACT_PATH \
--cache-server-path $CACHE_PATH \
$TOKEN_ARG"

echo ""
echo "------------------------------------------"

if [ "$choice" == "1" ]; then
    echo "🔵 Running: Build & Test (Self-Hosted)..."
    CMD="act pull_request -W .github/workflows/build-and-test.yml $ACT_COMMON_ARGS"
    echo "👉 Executing: act ..."
    eval "$CMD 2>&1 | tee act_execution.log"
    ACT_EXIT_CODE=${PIPESTATUS[0]}

elif [ "$choice" == "2" ]; then
    echo "🟣 Running: Release Workflow (Container Mode)..."

    # Add this warning confirmation
    echo "⚠️  [SAFETY CHECK] You are about to run the Release Workflow locally."
    echo "   If your release.yml is correctly configured with 'dry_run: \${{ env.ACT == 'true' }}',"
    echo "   this will be a simulation only."
    echo "   However, if that config is missing, this MIGHT push tags/commits to GitHub."
    echo ""
    read -p "❓ Do you want to proceed? (y/N) " confirm
    if [[ ! "$confirm" =~ ^(yes|y)$ ]]; then
        echo "🚫 Aborted by user."
        exit 0
    fi

    echo "🟣 Running: Release Workflow (Self-Hosted)..."
    CMD="act push -W .github/workflows/release.yml $ACT_COMMON_ARGS"
    echo "👉 Executing: act ..."
    eval "$CMD 2>&1 | tee act_execution.log"
    ACT_EXIT_CODE=${PIPESTATUS[0]}

elif [ "$choice" == "3" ]; then
    echo "🟢 Running: Release Logic Check..."
    if ! command -v npm &> /dev/null; then
        echo "❌ Error: npm missing."
        exit 1
    fi
    export GITHUB_TOKEN=$EXPORT_TOKEN
    export GITHUB_RUN_NUMBER=9999

    if [ ! -d "node_modules" ]; then
        echo "📦 Installing dependencies..."
        npm install
    fi

    echo "⚡ Executing semantic-release..."
    npx semantic-release --dry-run --branches "$(git branch --show-current)" --no-ci
    ACT_EXIT_CODE=$?
else
    echo "❌ Invalid option."
    exit 1
fi

echo ""
echo "=========================================="
if [ $ACT_EXIT_CODE -eq 0 ]; then
    echo "✅ Success!"
else
    echo "❌ Failed (Exit Code: $ACT_EXIT_CODE)"
fi
echo "=========================================="

echo ""
read -p "🧹 Clean up artifacts? [y/N] " response
response=$(echo "$response" | tr '[:upper:]' '[:lower:]')
if [[ "$response" =~ ^(yes|y)$ ]]; then
    ./gradlew clean
    rm -rf "$ARTIFACT_PATH" "$CACHE_PATH" "$XDG_CACHE_HOME"
    echo "✨ Cleanup complete!"
fi

exit $ACT_EXIT_CODE

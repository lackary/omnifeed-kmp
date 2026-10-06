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

mkdir -p "$ARTIFACT_PATH" "$CACHE_PATH"

# ==============================================================================
# 🔑 Secrets & Token Management (Merge Strategy)
# ==============================================================================
USER_SECRETS=".secrets"       # Your manual keys (Unsplash, Google, etc.)
RUN_SECRETS=".secrets.run"    # Temp file for execution (User keys + Dynamic Tokens)

# 1. Start with a fresh temp file
: > "$RUN_SECRETS"

# 2. Copy user secrets if they exist
if [ -f "$USER_SECRETS" ]; then
    echo "📝 Loading keys from $USER_SECRETS..."
    cat "$USER_SECRETS" >> "$RUN_SECRETS"
    echo "" >> "$RUN_SECRETS"
else
    echo "⚠️  $USER_SECRETS not found. Creating a template..."
    cat <<EOF > "$USER_SECRETS"
UNSPLASH_ACCESS_KEY=dummy_val
UNSPLASH_SECRET_KEY=dummy_val
GOOGLE_SERVICES_WEB_CLIENT_ID=dummy_val
GOOGLE_CLIENT_ID=dummy_val
GOOGLE_REVERSED_CLIENT_ID=dummy_val
EOF
    cat "$USER_SECRETS" >> "$RUN_SECRETS"
    echo "" >> "$RUN_SECRETS"
    echo "⚠️  Template created. Some tests may fail without real keys."
fi

# 3. Retrieve Token from 'gh' and append to temp file
if ! command -v gh &> /dev/null; then
    echo "⚠️  GitHub CLI (gh) not detected. Release steps will fail."
else
    RAW_TOKEN=$(gh auth token 2>/dev/null)
    if [ -n "$RAW_TOKEN" ]; then
        echo "✅ GitHub Token auto-detected from 'gh'."
        echo "# --- Dynamic Tokens ---" >> "$RUN_SECRETS"
        echo "GITHUB_TOKEN=$RAW_TOKEN" >> "$RUN_SECRETS"
        echo "SEMANTIC_RELEASE_TOKEN=$RAW_TOKEN" >> "$RUN_SECRETS"
    else
        echo "⚠️  gh is installed but not logged in."
    fi
fi

# ==============================================================================
# Menu
# ==============================================================================
echo ""
echo "Select Workflow:"
echo "  1) Full CI (.github/workflows/ci.yml)"
echo "     - Runs all jobs in CI workflow"
echo "  2) KMP Unit Tests Only (-j test)"
echo "     - Fast local check for unit tests"
echo "  3) Release Workflow (.github/workflows/release.yml)"
echo "     - Simulates semantic-release"
echo "  4) Release Logic Check (Host Mode)"
echo "     - Runs semantic-release directly (Fastest, no docker)"
echo ""
read -p "Enter option [1, 2, 3, or 4] (Default 1): " choice
choice=${choice:-1}

# Hybrid / Self-Hosted Mode Configuration:
# - Map ubuntu-latest to -self-hosted so local act runs on macOS host natively (No Docker required)
unset ANDROID_PREFS_ROOT
ACT_COMMON_ARGS="--platform macos-latest=-self-hosted \
--platform macos-26=-self-hosted \
--platform ubuntu-latest=-self-hosted \
--env ACT=true \
--env ANDROID_PREFS_ROOT= \
--secret-file $RUN_SECRETS \
--artifact-server-path $ARTIFACT_PATH \
--cache-server-path $CACHE_PATH"

echo ""
echo "------------------------------------------"

if [ "$choice" == "1" ]; then
    echo "🔵 Running: Build & Test (Full CI)..."
    CMD="act push -W .github/workflows/ci.yml $ACT_COMMON_ARGS"
    echo "👉 Executing: act ..."
    eval "$CMD 2>&1 | tee act_execution.log"
    ACT_EXIT_CODE=${PIPESTATUS[0]}

elif [ "$choice" == "2" ]; then
    echo "🔵 Running: KMP Unit Tests Only..."
    CMD="act push -W .github/workflows/ci.yml -j test $ACT_COMMON_ARGS"
    echo "👉 Executing: act ..."
    eval "$CMD 2>&1 | tee act_execution.log"
    ACT_EXIT_CODE=${PIPESTATUS[0]}

elif [ "$choice" == "3" ]; then
    echo "🟣 Running: Release Workflow (Container Mode)..."
    echo "⚠️  [SAFETY CHECK] You are about to run the Release Workflow locally."
    echo "   Ensure 'dry_run' logic is active in your YAML."
    echo ""
    read -p "❓ Do you want to proceed? (y/N) " confirm
    if [[ ! "$confirm" =~ ^(yes|y)$ ]]; then
        echo "🚫 Aborted by user."
        rm "$RUN_SECRETS" 2>/dev/null
        exit 0
    fi

    CMD="act push -W .github/workflows/release.yml $ACT_COMMON_ARGS"
    echo "👉 Executing: act ..."
    eval "$CMD 2>&1 | tee act_execution.log"
    ACT_EXIT_CODE=${PIPESTATUS[0]}

elif [ "$choice" == "4" ]; then
    echo "🟢 Running: Release Logic Check..."
    if ! command -v npm &> /dev/null; then
        echo "❌ Error: npm missing."
        exit 1
    fi
    export GITHUB_TOKEN=$RAW_TOKEN
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
    rm "$RUN_SECRETS" 2>/dev/null
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

# Cleanup the temporary secrets file
rm "$RUN_SECRETS" 2>/dev/null

echo ""
read -p "🧹 Clean up artifacts? [y/N] " response
response=$(echo "$response" | tr '[:upper:]' '[:lower:]')
if [[ "$response" =~ ^(yes|y)$ ]]; then
    ./gradlew clean
    rm -rf "$ARTIFACT_PATH" "$CACHE_PATH"
    echo "✨ Cleanup complete!"
fi

exit $ACT_EXIT_CODE

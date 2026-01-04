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
    # Ensure there's a newline at the end to prevent variable concatenation
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
    # Copy the template to run secrets as well
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

        # 🔥 Appending Tokens to the temp secrets file
        echo "# --- Dynamic Tokens ---" >> "$RUN_SECRETS"
        echo "GITHUB_TOKEN=$RAW_TOKEN" >> "$RUN_SECRETS"

        # 👇 This is the fix for your Release Workflow!
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
echo "  1) Build & Test (CI)"
echo "     - Runs .github/workflows/ci.yml"
echo "     - Uses local Mac environment"
echo ""
echo "  2) Release Workflow"
echo "     - Runs .github/workflows/release.yml"
echo "     - Simulates semantic-release"
echo ""
echo "  3) Release Logic Check (Host Mode)"
echo "     - Runs semantic-release directly (Fastest, no docker)"
echo ""
read -p "Enter option [1, 2, or 3] (Default 1): " choice
choice=${choice:-1}

# Hybrid Mode Configuration:
# - We now use --secret-file $RUN_SECRETS to include everything
ACT_COMMON_ARGS="--platform macos-latest=-self-hosted \
--platform ubuntu-latest=catthehacker/ubuntu:full-22.04 \
--container-architecture linux/amd64 \
--env ACT=true \
--secret-file $RUN_SECRETS \
--artifact-server-path $ARTIFACT_PATH \
--cache-server-path $CACHE_PATH"

echo ""
echo "------------------------------------------"

if [ "$choice" == "1" ]; then
    echo "🔵 Running: Build & Test (CI)..."
    # ✅ Update: Pointing to the renamed ci.yml
    CMD="act push -W .github/workflows/ci.yml $ACT_COMMON_ARGS"
    echo "👉 Executing: act ..."
    eval "$CMD 2>&1 | tee act_execution.log"
    ACT_EXIT_CODE=${PIPESTATUS[0]}

elif [ "$choice" == "2" ]; then
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

elif [ "$choice" == "3" ]; then
    echo "🟢 Running: Release Logic Check..."
    if ! command -v npm &> /dev/null; then
        echo "❌ Error: npm missing."
        exit 1
    fi
    # Use the token we captured earlier
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
    rm -rf "$ARTIFACT_PATH" "$CACHE_PATH" "$XDG_CACHE_HOME"
    echo "✨ Cleanup complete!"
fi

exit $ACT_EXIT_CODE

#!/bin/bash

# Exit immediately if any command fails
set -e

# 1. Generate auto-commit message using current datetime
CURRENT_DATE=$(date +"%Y-%m-%d %H:%M:%S")
COMMIT_MSG="Auto-release: $CURRENT_DATE"

# 2. Deterministically get the next tag
LATEST_TAG=$(git describe --tags --abbrev=0 2>/dev/null || echo "")

if [ -z "$LATEST_TAG" ]; then
    # If no tags exist in the repo yet, start at v1.0.0
    NEW_TAG="v1.0.0"
else
    # Remove the 'v' prefix if it exists
    VERSION=${LATEST_TAG#v}
    
    # Split the version into major, minor, and patch arrays using the '.' delimiter
    IFS='.' read -r -a VERSION_PARTS <<< "$VERSION"
    
    MAJOR="${VERSION_PARTS[0]:-1}"
    MINOR="${VERSION_PARTS[1]:-0}"
    PATCH="${VERSION_PARTS[2]:-0}"
    
    # Increment the patch version deterministically
    PATCH=$((PATCH + 1))
    
    # Construct the new tag
    NEW_TAG="v$MAJOR.$MINOR.$PATCH"
fi

echo "🚀 Publishing $NEW_TAG to GitHub..."

# Add all changed files
git add .

# Only commit if there are actually changes to commit
if ! git diff-index --quiet HEAD 2>/dev/null; then
    git commit -m "$COMMIT_MSG"
else
    echo "ℹ️ No new code changes detected. Just generating a new release tag."
fi

# Push the code to the main branch
echo "⏳ Pushing code..."
git push origin main

# Create the Git tag for JitPack
echo "🏷️ Creating tag $NEW_TAG..."
git tag "$NEW_TAG"

# Push the tag to GitHub
echo "⏳ Pushing tag..."
git push origin "$NEW_TAG"

echo "✅ Successfully pushed $NEW_TAG! JitPack will now build it."

#!/bin/bash

set -e # exit on first error

PUSH_OPTIONS=""
DOCKER=docker

# If using podman, make the necessary tweaks to push docker-format manifests
# Cloud Run is picky about this unfortunately
command -v podman &>/dev/null \
	&& DOCKER=podman \
	&& PUSH_OPTIONS="--remove-signatures" \
	&& export BUILDAH_FORMAT=docker \
	&& echo "using podman for builds"

REG_NAME="eu.gcr.io/quizzestutor/frontend-gc"
VERSION="$(git rev-parse HEAD)"

# TODO: pass this variables in here in a better way
FENIX_CLIENT_ID="${FENIX_CLIENT_ID:-1695915081466032}"
FRONTEND_BASE_URL="${FRONTEND_BASE_URL:-https://quiztutor.breda.pt}"
BACKEND_BASE_URL="${BACKEND_BASE_URL:-https://backend.quiztutor.breda.pt}"

# compute image tags
TAGS=(
	"$REG_NAME:$VERSION"
	"$REG_NAME:last"
)
BRANCH_LIST="$(git branch --points-at HEAD -a --format "%(refname:short)")"
(grep "^origin/master$" <<<$BRANCH_LIST) && TAGS+="$REG_NAME:stable"
(grep "^origin/develop$" <<<$BRANCH_LIST) && TAGS+="$REG_NAME:stable"

function tags_as_options() {
	for tag in $TAGS; do
		echo -n " -t $tag"
	done
}

# Ensure GCR credentials are available
gcloud auth configure-docker

# Build "regular" frontend image
pushd ../../frontend
$DOCKER build --build-arg NODE_ENV=production --build-arg FENIX_CLIENT_ID=$FENIX_CLIENT_ID --build-arg FRONTEND_BASE_URL=$FRONTEND_BASE_URL --build-arg BACKEND_BASE_URL=$BACKEND_BASE_URL -t "quizzestutor-frontend" .
popd

# Build Google Cloud-specialized fronend image (requires the former)
$DOCKER build $(tags_as_options) .

# Publish Google Cloud-specialized image to Container Registry
for tag in $TAGS; do
	$DOCKER push "$tag" $PUSH_OPTIONS
done

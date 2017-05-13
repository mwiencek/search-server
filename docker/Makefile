.PHONY: all build

all: build

IMAGE_NAME:=2017-q2

build:
	docker build -t metabrainz/search-server:$(IMAGE_NAME) -f ../Dockerfile-server ../
	docker build -t metabrainz/search-indexer:$(IMAGE_NAME) -f ../Dockerfile-indexer ../

push:
	docker push metabrainz/search-server:$(IMAGE_NAME)
	docker push metabrainz/search-indexer:$(IMAGE_NAME)

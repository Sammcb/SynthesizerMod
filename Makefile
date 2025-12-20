.PHONY: build clean lint generateSources client server
.DEFAULT_GOAL := build

generateSources:
	rm -rf mcSrc.nosync
	gradle genSources
	# Copy out common code
	cp .gradle/loom-cache/minecraftMaven/net/minecraft/minecraft-common-*/*-loom.mappings.*/minecraft-common-*-loom.mappings.*-sources.jar mcSrc.zip
	unzip -q mcSrc.zip -d mcSrc.nosync
	rm -rf mcSrc.zip
	# Copy out client code
	cp .gradle/loom-cache/minecraftMaven/net/minecraft/minecraft-clientOnly-*/*-loom.mappings.*/minecraft-clientOnly-*-loom.mappings.*-sources.jar mcSrc.zip
	unzip -q mcSrc.zip -d mcSrc.nosync
	rm -rf mcSrc.zip

build:
	gradle build

clean:
	gradle clean
	gradle --stop
	rm -rf .gradle
	rm -rf .kotlin
	rm -rf .project
	rm -rf .settings
	rm -rf run
	rm -rf mcSrc.nosync

server:
	gradle runServer

client:
	gradle runClient

lint:
	zizmor .
	editorconfig-checker
	flake-checker --no-telemetry
	nix flake check

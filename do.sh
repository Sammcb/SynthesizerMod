#!/bin/sh

MINECRAFT_JAVA=~/Library/Application\ Support/minecraft/runtime/java-runtime-gamma/mac-os-arm64/java-runtime-gamma/jre.bundle/Contents/Home

print_info() {
	printf "\e[1;35m$1\e[0m - \e[0;37m$2\e[0m\n"
}

help() {
	print_info help "Display callable targets"
	print_info up "Start Docker containers"
	print_info down "Stop and destroy running containers"
	print_info clean "Stop and aggressively remove everything"
	print_info project_clean "Clean build and downloaded gradle files (run local_clean first)"
	print_info local_clean "Clean directories on local machine created by gradle run"
	print_info init "Download gradle wrapper and build project"
	print_info run_client "Run Minecraft client"
	print_info run_server "Run Minecraft server"
	print_info generate_source "Generate Minecraft source files"
}

up() {
	docker compose up -d --build
}

down() {
	docker compose down
}

clean() {
	docker compose down --rmi 'all' -v --remove-orphans
	docker container prune -f
	docker image prune -af
	docker volume prune -f
	docker system prune -f
}

project_clean() {
	gradle clean
	./gradlew --stop
	rm -rf .gradle
	find gradle -not -name 'gradle' -not -name 'libs.versions.toml' -delete
	rm -rf run
	rm -rf remappedSrc
	rm -rf mcSrc.nosync
	rm -f gradlew.bat
	rm -f gradlew
}

local_clean() {
	export JAVA_HOME=$MINECRAFT_JAVA
	./gradlew --stop || true
	rm -rf ~/.gradle
	rm -rf ~/.hawtjni
}

init() {
	gradle wrapper --distribution-type all
	rm -f gradlew.bat
	./gradlew build
}

run_client() {
	export JAVA_HOME=$MINECRAFT_JAVA
	./gradlew runClient
}

run_server() {
	export JAVA_HOME=$MINECRAFT_JAVA
	./gradlew runServer
}

generate_source() {
	rm -rf mcSrc.nosync
	./gradlew genSources
	cp .gradle/quilt-loom-cache/*.*.*/*mappings*/minecraft-project-@-merged-named-sources.jar mcSrc.zip
	unzip -q mcSrc.zip -d mcSrc.nosync
	rm -f mcSrc.zip
}

if [ ${1:+x} ]; then
	$1
else
	help
fi
	
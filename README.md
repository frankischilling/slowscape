# SlowScape

SlowScape is a RuneLite plugin for the SlowScape challenge, where the account
does one counted thing per day.

This repository is currently a Plugin Hub-ready skeleton. Gameplay tracking
features will be added in later changes.

## Rules

The current SlowScape rules are documented in [SLOWSCAPE-RULES.MD](SLOWSCAPE-RULES.MD).

## Development

Use Java 11 when building or running the plugin:

```bash
JAVA_HOME=/usr/lib/jvm/java-11-openjdk ./gradlew test
JAVA_HOME=/usr/lib/jvm/java-11-openjdk ./gradlew run
```

If you use a Jagex account, follow RuneLite's development-client login guide:
https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts

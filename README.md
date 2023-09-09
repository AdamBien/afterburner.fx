# afterburner.fx

The opinionated just-enough [MVP](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter) framework (2.5 classes) for JavaFX.
This variant offers a more fluent API and more features.

Afterburner is a "Just-Enough-Framework" extracted from [airhacks-control](https://github.com/AdamBien/airhacks-control) and used in [airpad](https://github.com/AdamBien/airpad), [lightfish](https://github.com/AdamBien/lightfish) and [floyd](https://github.com/AdamBien/floyd) applications.

Goal: "Less Code, Increased Productivity"

Simplistic example: <https://github.com/JabRef/afterburner.fx/tree/main/demo-app>

See also: <http://afterburner.adam-bien.com>


## Release a new version to maven central

Check gpg key and export:
```bash
gpg --list-keys
gpg -K --keyid-format short
gpg --keyring secring.gpg --export-secret-keys > ~/.gnupg/secring.gpg
```

fill out gradle.properties with
```
signing.keyId=<last 8 digits of gpg key (short format) >
signing.password=<gpg key passwrod>
signing.secretKeyRingFile=~/.gnupg/secring.gpg

ossrhUsername=<nexus ossrh username>
ossrhPassword=<nexus ossrh password>
```

When all is set: (Warning: This step cannot be undone!)

```
./gradlew publishToSonatype closeAndReleaseSonatypeStagingRepository
```


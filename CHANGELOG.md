# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]

## Added

- Demo application
- Method `setAsContent(DialogPane)` to create dialogs easier
- `ResourceLocator` (to load CSS classes)
- Added `injectMembers` and `instantiatePresenter` methods to PresenterFactory for late injection 

## Changed

- Completely rewritigen view loader using fluent interface
- Switch from Java Util Logging to SLF4J
- Switch from javax to jakarta

## 1.0.0 - 2018-02-07

Initial release.

### Added

- Version based on [version 1.7.0 of Adam Bien's afterburner.fx](https://github.com/AdamBien/afterburner.fx)
- Integrated https://github.com/AdamBien/afterburner.fx/pull/63
- Integrated https://github.com/AdamBien/afterburner.fx/pull/68
- Integrated https://github.com/AdamBien/afterburner.fx/pull/80
- Integrated https://github.com/AdamBien/afterburner.fx/pull/72, https://github.com/AdamBien/afterburner.fx/pull/73, https://github.com/AdamBien/afterburner.fx/pull/74
- `build.gradle` to enable building with gradle

[Unreleased]: https://github.com/JabRef/afterburner.fx/compare/1.0.0...HEAD

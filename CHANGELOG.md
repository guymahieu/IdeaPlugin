<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Axon Framework plugin Changelog

## [0.5.1]

### Fixed

- Fix popup on deadline manager methods when there are qualified references as arguments. Fixes #16

## [0.5.0]

### Changed

- Improve list rendering for better performance
- Restructured packages
- Improve caching on line markers to improve performance

## [0.4.2]

### Changed

- Always show popup for references, even if there is only one.

## [0.4.1]

### Changed

- Split deadline resolver from normal message creation resolver for performance improvements

### Added

- Utility to cache information on PsiElement level to improve performance

## [0.4.0]

### Fixed

- Fixed issue with deadline cancel methods not always being detected correctly.

## [0.3.0]

### Fixed

- Ghost line marker on aggregate constructors
- Fixed event handlers showing as "Event Processor". They will now show the name of the containing class

### Added

- Line marker on aggregate members to easily related models with the aggregate model hierarchy.

## [0.2.0]

### Changed

- New icons added based on the new logo
- Intercepted command handlers now have their own icon

### Added

- Rewritten from scratch for Axon Framework 4.0
- Added support for Kotlin in addition to Java
- Add line marker on handler methods, such as Command-, Event- and DeadlineHandlers
- Add line marker on constructors of classes that are used as payload, for navigation to handlers
- Add line marker on scheduling of deadlines, for navigation to deadline handlers
- Add line marker on class declarations of classes that are used as payloads, for navigation to both constructors and handlers
- Add line marker on command handlers that are intercepted by a `@CommandHandlerInterceptor` annotation
- Add usage provider which marks methods (and payloads) used by Axon Framework as used
- Add inspection for missing empty constructor in aggregates
- Add inspection for missing identifier in aggregates
- Add inspection for missing @EntityId annotation when required
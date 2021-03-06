# Changelog

<!--lint disable no-duplicate-headings-->

## Vx.x.x (Pre-release)

### Release Highlights

N/A

### Important Notes

N/A

### Breaking Changes

N/A

## 1.1.0

### Release Highlights

...GraalVM, MKDocs, DemoCases

### Important Notes

- Repository shift to github.com

### Breaking Changes

...

## 1.0.1

### Release Highlights

Fix logging (log4j config)

### Important Notes

src/main/resources/log4j.xml changed to src/main/resources/log4j2.xml

### Breaking Changes

N/A

## 1.0.0

Adoption of rapiddweller-benerator-ce by [rapiddweller](https://rapiddweller.com). Project was hard forked, updated and tidied however no logical
changes have occurred since databene-benerator v0.9.8 was released by Volker Bergmann.

### Release Highlights

- Project and dependency update to Java 1.8 and Jave 11
- Update shop demo cases to get started
- Repository shift to gitlab.com (Build pipeline, E2ET)

### Important Notes

N/A

### Breaking Changes

N/A

### Changes since 0.9.8

- Migration of [databene-benerator](https://sourceforge.net/projects/benerator/)
  to [rapiddweller](https://rapiddweller.com)
- Update project and dependencies to Java 1.8 and Jave 11
- Migrate and update databene dependencies (e.g. databene-commons -> rapidweller-common)
- Create automated build pipeline
- Create README, CHANGELOG, CONTRIBUTING, RELEASE pages
- Minor improvements of code
- Adjust unit tests to align with new java and dependency versions
- Add E2ET for several DB for shop demo
- Rename project from org.databene.benerator to com.rapiddweller.benerator

### Changes before v0.9.8

- Please see previous [releasenotes](/releasenotes) in the releasenotes project folder.

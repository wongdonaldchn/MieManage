# nablarch-biz-sample-all

| master | develop |
|:-----------|:------------|
|[![Build Status](https://travis-ci.org/nablarch/nablarch-biz-sample-all.svg?branch=master)](https://travis-ci.org/nablarch/nablarch-biz-sample-all)|[![Build Status](https://travis-ci.org/nablarch/nablarch-biz-sample-all.svg?branch=develop)](https://travis-ci.org/nablarch/nablarch-biz-sample-all)|

## dependency library

This library need to be installed manually:
library             |file name        |group ID       |artifact ID          |version   |
:-------------------|:----------------|:--------------|:--------------------|:------------|
[kaptcha](https://code.google.com/archive/p/kaptcha/downloads) |kaptcha-2.3.2.jar |com.google.code |kaptcha              |2.3.2        |

Please use the command below:

```
mvn install:install-file -Dfile=kaptcha-2.3.2.jar -DgroupId=com.google.code -DartifactId=kaptcha -Dversion=2.3.2 -Dpackaging=jar
```


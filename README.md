# drwolf-push

## Install

Install via jitpack: [https://jitpack.io/#DrWolf-OSS/drwolf-push](https://jitpack.io/#DrWolf-OSS/drwolf-push)

## Usage

```java
Push push = new Push("GCM-KEY","path/to/apple-cert.p12","apple.cert.password", "Title");

int badgeNumber = 1;
String sound = "default"

push.send("message","pushToken", badgeNumber, sound);

```

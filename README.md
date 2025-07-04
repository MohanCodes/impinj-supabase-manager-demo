# IMPINJ Supabase Setup Guide

A complete setup guide for connecting an IMPINJ RFID reader with Java and Supabase integration. Just a better formatted version of the info.txt

## Hardware Setup

### Equipment Used
- **IMPINJ Reader**: IPJ-REV-R220
- **Power**: DC 24V power adapter
- **Connection**: Ethernet cable (reader to computer)

### Physical Setup
1. Connect the DC 24V power adapter to the IMPINJ reader
2. Connect an ethernet cable from the IMPINJ ethernet port directly to your computer's ethernet port
3. Note the last three couplets from the MAC address sticker on the side of the reader (format: XX-XX-XX)

### Web Interface Access
- Navigate to: `http://speedwayR-[last-three-couplets]/`
- Example: `http://speedwayR-15-25-B4/` (using the last three couplets noted above)
- **Login credentials**: `root` / `impinj`

## Software Requirements

### Java Environment
- **Java Version**: Java SE-1.8 (required for compatibility with IMPINJ software 6.4.1.240)
- **IDE**: Eclipse (recommended)

### IMPINJ Software
- **Octane SDK**: Version 3.0.0 (matches IMPINJ software version 6.4.1.240)
- **IMPINJ Software Version**: 6.4.1.240

## Installation Steps

### 1. Install Octane SDK
Follow the official installation guide:
- [Octane SDK Installation Instructions](https://support.impinj.com/hc/en-us/articles/360010077479-Octane-SDK-Installation-Instructions)
- [Setting up Octane SDK Java with Eclipse](https://support.impinj.com/hc/en-us/articles/219015497-Setting-up-Octane-SDK-Java-with-Eclipse)

### 2. Setup Java Project
1. Create a new Java project in Eclipse using Java SE-1.8
2. Set up the `com.examples` Java files from the Octane SDK
3. Create your own package with your preferred name

### 3. Add Required Libraries

#### Apache HttpClient Dependencies
Download from [Apache HttpComponents Downloads](https://hc.apache.org/downloads.cgi):
- `commons-logging`
- `httpclient` (version 4.5.14 recommended)
- `httpcore`

**Note**: HttpClient 4.5.14 is recommended as older versions tend to be more stable. The additional HttpClient library is required because native Http was only released in Java 11.

#### Library Setup
1. Add all downloaded JAR files to your project's `lib` folder
2. **Important**: Configure the build path for all libraries you plan to use to avoid compilation errors

## Configuration Files

### 1. log4j.properties
Create `src/log4j.properties` (may differ slightly from IMPINJ documentation):

```properties
# Root logger option
log4j.rootLogger=INFO, file

# Direct log messages to a log file
log4j.appender.file=org.apache.log4j.RollingFileAppender
log4j.appender.file.File=C:\\temp\\javaSDK\\log.out
log4j.appender.file.MaxFileSize=10MB
log4j.appender.file.MaxBackupIndex=10
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n
```

### 2. config.properties
Create `config.properties` with your Supabase credentials:

```properties
supabase.url=YOUR_SUPABASE_URL
supabase.key=YOUR_SUPABASE_KEY
```

## Database Setup (Supabase)

### SQL Table Structure
Execute the following SQL in your Supabase database:

```sql
CREATE TABLE public.test (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    test TEXT -- This is the column we will update
);

-- Optional: Insert a test row
INSERT INTO public.test (test) VALUES ('Hello World');
```

## Version Compatibility Notes

- **Java SE-1.8**: Required for compatibility with IMPINJ software 6.4.1.240
- **Octane SDK 3.0.0**: Matches IMPINJ software version 6.4.1.240
- **HttpClient 4.5.14**: Stable version, compatible with older Java versions
- **IMPINJ Software**: Version 6.4.1.240

## Troubleshooting Tips

1. **Red underline errors**: Ensure all libraries are added to the build path
2. **Connection issues**: Verify the MAC address couplets are correct in the web URL
3. **Java compatibility**: Stick with Java SE-1.8 for best compatibility
4. **Library conflicts**: Use the specified HttpClient version (4.5.14) to avoid compatibility issues

## Quick Reference

- **Web Interface**: `http://speedwayR-[MAC-last-three-couplets]/`
- **Login**: `root` / `impinj`
- **Java Version**: SE-1.8
- **SDK Version**: Octane 3.0.0
- **HttpClient Version**: 4.5.14

This setup should work for anyone following these exact steps and version requirements.
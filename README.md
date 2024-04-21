This utility allows you to create a local working copy of a remote Wiki.js server. With that copy, you can modify pages locally with any text editor you want. All changes you make will be uploaded to the remote server during the synchronization process.

## Features
- Deliver any creations/updates/moves/deletions of remote Wiki.js pages to the local working copy.
- Deliver any updates/deletions of local working copy pages to remote Wiki.js pages.
- Conflict resolution: you can accept remote/local changes or merge them manually.
- Support HTTPS-connection with self-signed certificates.

## Requirements
- JRE17

## How to Use
### Configuring
Working with the utility starts with creating an `application.properties` file (in the utility's working directory):

```properties
# URL to the remote Wiki.js server
wiki.js.server  = https://wiki.example.com

# URL to the GraphQL endpoint on the remote Wiki.js server
wiki.js.graphql.endpoint    = ${wiki.js.server}/graphql

# Your username for authorization on the remote Wiki.js server
auth.user       = ${auth_user}

# Your password for authorization on the remote Wiki.js server
auth.password   = ${auth_password}

# Authorization type (local/ldap/gitlab/etc)
auth.type       = ldap

# Local folder path to your working copy
wiki.js.pages.repository    = ./repo

# Location for the inner utility Database file
wiki.js.db                  = ./ws.db

# Location for own trust store for self-signed certificates
   cacerts = ./cacerts
```

There is support for placeholders ${placeholder-name} in the configuration file. Placeholders will be replaced by:
- the value of another option in this configuration file with the name `placeholder-name` (if exists)
- a system environment variable with the name `placeholder-name`

If a placeholder cannot be resolved, an exception will be thrown.

### Run
The utility can be run without any required arguments. At first run, it will ask for acceptance of the certificate, if needed (you must accept it to proceed). It will then create its own database and working copy with all remote Wiki.js page copies. On subsequent runs, the utility will perform synchronization between your working copy and the remote Wiki.js server.

#### Command-line options
- `-f`, `--force` - allow to always apply the selected conflict resolution strategy (postpone/mine/theirs). Enabling this option prevents asking for the resolution strategy for all conflicted pages.
- `--always-cert-trust` - allow to always trust all Wiki.js server certificates. Enabling this option prevents asking to accept certificate(s) each time it will be changed.

### Manual Conflicts Resolution
If you select the postpone conflict resolution strategy for a page, then there will be two page versions in the working copy:
- your version, named as usual (say, `page-1.md`)
- the remote version, named with the postfix `remote` (`page-1.remote.md`)

To resolve the conflict, you must create (or rename) a file with the postfix `resolved` (`page-1.resolved.md`), that contains the merged result page. In the next utility run, all other versions (`page-1.md`, `page-1.remote.md`) will be removed, the merged page (`page-1.resolved.md`) will be renamed without the postfix (`page-1.md`) and its content will be uploaded to the remote Wiki.js server page counterpart.

## Restrictions
- The utility only works with pages. Attachment synchronization is not supported.
- Do not create/move pages in the working copy - it is not supported.

## Roadmap
### v2.0
- Add support sync pages and assets separately
- Processing asset links on pages (replace by `file://`)
- Optimize GraphQL queries for fetch asset list (batch/combine)

### v2.1
- Link consistent checker (List all invalid links, that can be appeared with page rename/moving)
- Add move page/asset operation with auto-update links (cli-command `move`)
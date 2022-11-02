# Web Interface for CQP
## Backend
### Minimum requirements
- Java 15 (temporary, later Java 8)

### Usage
Call Server.main() of [Server.java](src/backend/src/main/java/Server.java) to start.

***

## Frontend
### Minimum requirements
- Node.js 14.x
- NPM 7.x

### Preparation
After this repository has been cloned and the minimum requirements are installed, run this command in the project root first to install all needed NPM package dependencies:
```shell
npm install
```

### Usage
In order to access the web interface, run these commands in the project root
to transpile needed frontend files and to automatically start the web server:
```shell
npm run build && npm run serve
```

### Development
In order to use the development environment,
run this command in the project root and start modifying the needed files in the `src` directory:
```shell
npm run dev
```

#### Libraries
For the UI the following libraries are primarily used:
- [Vite](https://vitejs.dev/) (Frontend Tooling library)
- [Vue](https://v3.vuejs.org/) (Progressive JS framework)
- [Vuex](https://www.smashingmagazine.com/2020/01/data-components-vue-js/#:~:text=3.%20Using%20Vuex) (Vue state management library)
- [Google Charts](https://developers.google.com/chart) (Chart library)
- [Bootstrap CSS](https://getbootstrap.com/docs/4.6/getting-started/introduction/) (CSS framework)# CQP

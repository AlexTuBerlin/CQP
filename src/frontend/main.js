import { createApp } from 'vue'
import { createStore } from 'vuex'
import axios from 'axios'
import App from './App.vue'

const app = createApp(App)

app.use(createStore({
    state: {
        isLoading: true,
        configuration: null,
        query: null,
        queryOptimization: null,
        queryPlan: [],
        queryResult: null
    },
    getters: {
        isLoading: state => {
            return state.isLoading
        },
        configuration: state => {
            return state.configuration
        },
        query: state => {
            return state.query
        },
        queryOptimization: state => {
            return state.queryOptimization
        },
        queryPlan: state => {
            return state.queryPlan
        },
        queryResult: state => {
            return state.queryResult
        },
        getTables: state => {
            // TODO
            return {
                "opportunities": ["id", "name"],
                "opportunities_custom": ["opp_status_c", "opp_funded_date_c"]
            }
        }
    },
    mutations: {
        setLoading (state, payload) {
            state.isLoading = payload
        },
        setConfiguration (state, payload) {
            state.configuration = payload
        },
        setQuery (state, payload) {
            state.query = payload
        },
        setQueryOptimization (state, payload) {
            state.queryOptimization = payload
        },
        setQueryPlan (state, payload) {
            state.queryPlan = payload
        },
        setQueryResult (state, payload) {
            state.queryResult = payload
        }
    },
    actions: {
        setLoading (context, payload) {
            context.commit('setLoading', payload)
        },
        setConfiguration (context, payload) {
            context.commit('setConfiguration', payload)
        },
        setQuery (context, payload) {
            context.commit('setQuery', payload)
        },
        setQueryOptimization (context, payload) {
            context.commit('setQueryOptimization', payload)
        },
        setQueryPlan (context, payload) {
            context.commit('setQueryPlan', payload)
        },
        setQueryResult (context, payload) {
            context.commit('setQueryResult', payload)
        }
    }
}))

app.mount('#app')

// Set axios base URL
axios.defaults.baseURL = 'http://localhost:8080/'
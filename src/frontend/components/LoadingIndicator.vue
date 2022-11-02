<template>
    <transition name="fade">
        <div v-if="isLoading" class="loader-bg">
            <div class="loader-track">
                <div class="loader-fill"></div>
            </div>
        </div>
    </transition>
</template>

<script>
import axios from 'axios';

export default {
    name: 'LoadingIndicator',
    mounted() {
        this.enableInterceptor();
    },
    data() {
        return {
            axiosNumberOfRequestsPending: 0,
            axiosInterceptor: null
        }
    },
    computed: {
        isLoading() {
            return this.$store.getters.isLoading;
        }
    },
    methods: {
        enableInterceptor() {
            this.axiosInterceptor = axios.interceptors.request.use((config) => {
                this.axiosNumberOfRequestsPending++;
                this.$store.dispatch('setLoading', true);
                return config;
            }, (error) => {
                this.axiosNumberOfRequestsPending--;
                if (this.axiosNumberOfRequestsPending < 1) {
                    this.$store.dispatch('setLoading', false);
                }
                return Promise.reject(error);
            });

            axios.interceptors.response.use((response) => {
                this.axiosNumberOfRequestsPending--;
                if (this.axiosNumberOfRequestsPending < 1) {
                    this.$store.dispatch('setLoading', false);
                }
                return response;
            }, (error) => {
                this.axiosNumberOfRequestsPending--;
                if (this.axiosNumberOfRequestsPending < 1) {
                    this.$store.dispatch('setLoading', false);
                }
                return Promise.reject(error);
            });
        },

        disableInterceptor() {
            axios.interceptors.request.eject(this.axiosInterceptor);
        }
    }
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.5s;
}

.fade-enter,
.fade-leave-to {
    opacity: 0;
}
</style>
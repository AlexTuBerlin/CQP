<template>
    <div class="card Recent-Users">
        <div class="card-header">
            <h5>Query Optimization</h5>
        </div>

        <div v-if="!configuration" class="card-block px-2 py-5 text-center">
            <span>Please load your configuration file</span>
        </div>

        <div v-if="configuration" class="card-block px-0 py-3">
            <div class="query-box p-l-10 p-r-10">
                <form>
                    <editor v-model="editorQuery" />
                    <div class="buttons p-t-5">
                        <div class="btn-group btn-group-sm btn-group-optimizer" role="group" aria-label="Basic example">
                            <button @click="optimizeQuery(OPTIMIZER_LEVEL.NONE, $event)" type="button" class="btn btn-outline-primary">No Optimization</button>
                            <button @click="optimizeQuery(OPTIMIZER_LEVEL.TRADITIONAL, $event)" type="button" class="btn btn-outline-primary">Traditional Optimization</button>
                            <button @click="optimizeQuery(OPTIMIZER_LEVEL.COMPLIANT, $event)" type="button" class="btn btn-outline-primary">Compliance-based Optimization</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <modal v-if="showModal" :key="modalText" :title="modalTitle" :text="modalText" @closeModal="closeModal" />
</template>

<script>
import Editor from './Editor.vue';
import Modal from './Modal.vue';
import axios from 'axios';
import { get } from 'lodash-es/object';

const OPTIMIZER_LEVEL = Object.freeze({
    NONE: 'none',
    TRADITIONAL: 'traditional',
    COMPLIANT: 'compliant'
});

export default {
    name: 'DatabaseQuery',
    components: {
        Editor,
        Modal
    },
    data() {
        return {
            OPTIMIZER_LEVEL,
            modalTitle: null,
            modalText: null,
            editorQuery: ''
        }
    },
    computed: {
        configuration() {
            return this.$store.getters.configuration
        },
        showModal() {
            return this.modalTitle && this.modalText;
        }
    },
    methods: {
        closeModal() {
            this.modalTitle = null;
            this.modalText = null;
        },
        communicationErrorAlert(res) {
            this.modalTitle = 'The following error response was received for the given query';
            this.modalText = (get(res, 'response.data.message')) ? res.response?.data?.message : res.message;
        },
        optimizeQuery(optimizerLevel, event) {
            const formData = new FormData();
            formData.set('query', this.editorQuery);
            formData.set('optimizationType', optimizerLevel);

            this.$store.dispatch('setQuery', this.editorQuery);
            this.$store.dispatch('setQueryOptimization', optimizerLevel);
            this.$store.dispatch('setQueryResult', null);

            axios.post('/api/databases/query/plan', formData).then(res => {
                let queryPlan = res.data.data.query.plan;

                event.target.parentNode.querySelectorAll('button').forEach(button => {
                    button.classList.remove('btn-primary');
                    button.classList.add('btn-outline-primary');
                });
                event.target.classList.remove('btn-outline-primary');
                event.target.classList.add('btn-primary');

                this.$store.dispatch('setQueryPlan', queryPlan);
            }).catch(this.communicationErrorAlert);
        }
    }
}
</script>

<style scoped>
.btn-group-optimizer {
    display: flex;
}

.btn-group-optimizer button {
    flex-grow: 1;
}
</style>
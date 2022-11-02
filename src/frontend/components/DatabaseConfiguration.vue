<template>
    <div class="card">
        <div class="card-header">
            <h5>Geo-distributed Database Configuration</h5>
        </div>
        <div class="card-body">
            <form @submit.prevent="handleSubmit">
                <div class="form-group">
                    <label for="configuration-file">Load configuration file</label>
                    <input @change="changeFile" type="file" class="form-control-file" id="configuration-file" required accept="application/json">
                </div>
                <div v-if="!connected" class="button">
                    <button type="submit" class="btn btn-sm">Upload configuration</button>
                </div>
                <div v-if="connected" class="button">
                    <button type="button" class="btn btn-sm btn-success">Connected</button>
                </div>
            </form>
        </div>
    </div>
    <modal v-if="showModal" :key="modalText" :title="modalTitle" :text="modalText" @closeModal="closeModal" />
</template>

<script>
import axios from 'axios';
import Modal from './Modal.vue';
import { get } from 'lodash-es/object';

export default {
    name: 'DatabaseConfiguration',
    components: {
        Modal
    },
    data() {
        return {
            file: null,
            modalTitle: null,
            modalText: null,
            connected: false
        }
    },
    computed: {
        showModal() {
            return this.modalTitle && this.modalText;
        }
    },
    methods: {
        changeFile(event) {
            this.connected = false;
            this.file = event.target.files.item(0);
        },
        communicationErrorAlert(res) {
            this.modalTitle = 'The following error response was received';
            this.modalText = (get(res, 'response.data.message')) ? res.response?.data?.message : res.message;

            this.connected = false;
            this.$store.dispatch('setConfiguration', null);
        },
        closeModal() {
            this.modalTitle = null;
            this.modalText = null;
        },
        handleSubmit() {
            const formData = new FormData();
            formData.set('file', this.file);

            let configuration = {};
            axios.post('/api/databases/configuration/create', formData).then((res) => {
                // Set configuration ID of configuration object
                configuration.configurationID = res.data.data ;
                // Set default axios query parameter for all following requests
                axios.defaults.params = { configurationID: configuration.configurationID };

                axios.get('/api/databases/schemas').then((res) => {
                    let requests = [];
                    configuration.schemas = res.data.data.schemas;
                    configuration.schemas.forEach((schema, index) => {
                        requests.push(
                            axios.get('/api/databases/relations', { params: { schemaID: schema.id } }).then((res) => {
                                configuration.schemas[index].relations = res.data.data.relations;
                            })
                        );
                        requests.push(
                            axios.get('/api/databases/constraints', { params: { schemaID: schema.id } }).then((res) => {
                                configuration.schemas[index].constraints = res.data.data.constraints;
                            })
                        );
                    });

                    Promise.all(requests).then(() => {
                        this.connected = true;
                        this.$store.dispatch('setConfiguration', configuration);
                    }).catch(this.communicationErrorAlert);
                }).catch(this.communicationErrorAlert);
            }).catch(this.communicationErrorAlert);
        }
    }
}
</script>

<style scoped>

</style>
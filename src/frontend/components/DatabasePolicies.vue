<template>
    <div class="card database-policies">
        <div class="card-header">
            <h5>Policy Specification</h5>
        </div>

        <div v-if="!configuration" class="card-block px-2 py-5 text-center">
            <span>Please load your configuration file</span>
        </div>

        <div v-if="configuration" class="card-block px-0 py-0">
            <tabs :options="{ useUrlFragment: false }"
                  cache-lifetime="0"
                  nav-class="nav nav-tabs"
                  nav-item-class="nav-item"
                  nav-item-link-class="nav-link"
                  nav-item-link-active-class="active"
                  panels-wrapper-class="card-body p-0 border-0 tab-content">
                <tab v-for="tab in tabs" :key="tab.id" :name="tab.name" panel-class="tab-pane">
                    <div class="table-responsive">
                        <form class="px-4 py-3">
                            <div class="form-group">
                                <label>Policy description</label>
                                <input v-model="newPolicyDataDescriptions[tab.id]" :key="newPolicyKeys[tab.id]" type="text" class="form-control" placeholder="Enter short description" required>
                            </div>
                            <div class="form-group">
                                <label>Policy database</label>
                                <select v-model="newPolicyDataSchemaIDs[tab.id]" :key="newPolicyKeys[tab.id]" class="form-control" required>
                                    <option :value="undefined" disabled selected>Please select database</option>
                                    <option v-for="option in getSchemaOptions(tab.name)" :value="option.value">
                                        {{ option.text }}
                                    </option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label>Policy expression</label>
                                <editor v-model="newPolicyDataConstraints[tab.id]" :key="newPolicyKeys[tab.id]" height="116" />
                            </div>
                            <div class="p-t-0">
                                <button @click="addPolicy(tab, $event)" type="button" class="btn btn-outline-primary btn-sm">Add</button>
                                <button @click="resetEditor(tab.id)" type="button" class="btn btn-outline-primary btn-sm">Reset</button>
                            </div>
                        </form>
                        <table class="table table-bordered scroll-bar mb-0">
                            <tbody>
                                <tr v-if="!getPolicies(tab.name).length">
                                    <td colspan="2" class="px-4 py-4">
                                        No policies exist in this location.
                                    </td>
                                </tr>
                                <tr v-for="policy in getPolicies(tab.name)">
                                    <td v-if="policy.editorOpen" colspan="2" class="policy-box p-l-10 p-r-10 p-t-10">
                                        <form>
                                            <div class="form-group">
                                                <label>Policy description</label>
                                                <input v-model="policy.description" type="text" class="form-control" placeholder="Enter short description">
                                            </div>
                                            <div class="form-group">
                                                <label>Policy expression</label>
                                                <editor v-model="policy.constraint" />
                                            </div>
                                            <div class="p-t-0">
                                                <button @click="patchPolicy(policy)" :class="{ 'disabled': policy.updatingPolicy }" type="button" class="btn btn-outline-primary btn-sm">Save</button>
                                                <button @click="togglePolicyEdit(policy, $event)" type="button" class="btn btn-outline-primary btn-sm">Cancel</button>
                                            </div>
                                        </form>
                                    </td>
                                    <td v-if="!policy.editorOpen">
                                        <h6 class="m-2">{{ policy.description }}</h6>
                                        <p class="m-2 policy-text">{{ policy.constraint }}</p>
                                    </td>
                                    <td v-if="!policy.editorOpen">
                                        <i @click="togglePolicyEdit(policy, $event)" class="fa fa-edit"></i>
                                        <i @click="togglePolicyStatus(policy)" :class="{ 'fa-toggle-on text-success': policy.enabled && !policy.updatingStatus, 'fa-toggle-off text-danger': !policy.enabled && !policy.updatingStatus, 'fa-spin fa-spinner': policy.updatingStatus }" class="fa"></i>
                                        <i @click="deletePolicy(policy)" :class="{ 'fa-trash-alt': !policy.deleting, 'fa-spin fa-spinner': policy.deleting }" class="fa"></i>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </tab>
            </tabs>
        </div>
    </div>
</template>

<script>
import axios from 'axios';
import { uniq } from 'lodash-es/array';
import { kebabCase } from 'lodash-es/string';
import Editor from './Editor.vue';
import {Tabs, Tab} from 'vue3-tabs-component';

export default {
    name: 'DatabasePolicies',
    data() {
        return {
            originalPolicy: [],
            newPolicyDataSchemaIDs: [],
            newPolicyDataDescriptions: [],
            newPolicyDataConstraints: [],
            newPolicyKeys: []
        }
    },
    components: {
        Tab,
        Tabs,
        Editor
    },
    computed: {
        configuration() {
            return this.$store.getters.configuration;
        },
        tabs() {
            return this.getTabsFromSchemas(this.configuration.schemas);
        }
    },
    methods: {
        getLocations(schemas, includeAll = true) {
            let locations = [];
            schemas.forEach(schema => {
                locations.push(...schema.locations);
            });
            if (includeAll) {
                locations.push('All');
            }

            return uniq(locations);
        },
        getPolicies(schemaName) {
            let policies = [];
            let schemas = this.configuration.schemas;
            if (schemaName !== 'All') {
                schemas = schemas.filter(schema => schema.locations.includes(schemaName));
            }
            schemas.forEach(schema => {
                schema.constraints.forEach(constraint => constraint.schemaID = schema.id);
                policies.push(...schema.constraints);
            });

            return policies;
        },
        getSchemaOptions(schemaName) {
            let schemaOptions = [];
            let schemas = this.configuration.schemas;
            if (schemaName !== 'All') {
                schemas = schemas.filter(schema => schema.locations.includes(schemaName));
            }
            schemas.forEach(schema => {
                schemaOptions.push({ text: schema.name, value: schema.id });
            });
            schemaOptions.sort((r1, r2) => r1.text.localeCompare(r2.text));

            return schemaOptions;
        },
        getTabsFromSchemas(schemas) {
            return this.getLocations(schemas, true).map(location => {
                let kebabCaseLocation = kebabCase(location);

                return {
                    id: 'tab-' + kebabCaseLocation,
                    name: location
                };
            });
        },
        patchPolicy(policy) {
            if (policy.updatingPolicy) return;
            policy.updatingPolicy = true;

            const formData = new FormData();
            formData.set('schemaID', policy.schemaID);
            formData.set('constraintID', policy.id);
            formData.set('description', policy.description);
            formData.set('constraint', policy.constraint);
            formData.set('status', policy.enabled);

            axios.patch('/api/databases/constraint/update', formData)
                .then(res => {
                    let constraint = res.data.data.constraint;

                    policy.description = constraint.description;
                    policy.constraint = constraint.constraint;
                    policy.editorOpen = false;
                })
                .catch(error => alert(error))
                .finally(() => delete policy.updatingPolicy);
        },
        togglePolicyEdit(policy, event) {
            let originalPolicyComposedIndex = policy.schemaID + '_' + policy.id;
            if (policy.editorOpen) {
                policy.description = this.originalPolicy[originalPolicyComposedIndex].description;
                policy.constraint = this.originalPolicy[originalPolicyComposedIndex].constraint;
                policy.editorOpen = false;
            } else {
                this.originalPolicy[originalPolicyComposedIndex] = { ...policy };
                policy.editorOpen = true;
                // Scroll table to left side
                event.target.closest('.table-responsive').scrollLeft = 0;
            }
        },
        togglePolicyStatus(policy) {
            if (policy.updatingStatus) return;
            policy.updatingStatus = true;

            const formData = new FormData();
            formData.set('schemaID', policy.schemaID);
            formData.set('constraintID', policy.id);
            formData.set('status', !policy.enabled);

            axios.patch('/api/databases/constraint/status', formData)
                .then(res => policy.enabled = res.data.data.constraint.enabled)
                .catch(error => alert(error))
                .finally(() => delete policy.updatingStatus);
        },
        deletePolicy(policy) {
            if (policy.deleting) return;
            policy.deleting = true;

            const formData = new FormData();
            formData.set('schemaID', policy.schemaID);
            formData.set('constraintID', policy.id);

            axios.delete('/api/databases/constraint/delete', { data: formData })
                .then(() => {
                    let schema = this.configuration.schemas.find(schema => schema.id === policy.schemaID);
                    let constraintIndex = schema.constraints.findIndex(constraint => constraint.id === policy.id);
                    schema.constraints.splice(constraintIndex, 1);
                })
                .catch(error => {
                    delete policy.deleting;
                    alert(error);
                });
        },
        addPolicy(tab, event) {
            if (!event.target.closest('form').reportValidity()) return;
            event.target.classList.add('disabled');

            let schemaID = this.newPolicyDataSchemaIDs[tab.id];
            let description = this.newPolicyDataDescriptions[tab.id];
            let constraint = this.newPolicyDataConstraints[tab.id];

            const formData = new FormData();
            formData.set('schemaID', schemaID);
            formData.set('description', description);
            formData.set('constraint', constraint);

            axios.post('/api/databases/constraint/add', formData)
                .then((res) => {
                    this.resetEditor(tab.id);

                    let schema = this.configuration.schemas.find(schema => schema.id === schemaID);
                    let constraint = res.data.data.constraint;
                    constraint.schemaID = schemaID;
                    schema.constraints.push(constraint);
                })
                .catch(error => alert(error))
                .finally(() => {
                    event.target.classList.remove('disabled');
                });
        },
        resetEditor(tabID) {
            delete this.newPolicyDataSchemaIDs[tabID];
            delete this.newPolicyDataDescriptions[tabID];
            delete this.newPolicyDataConstraints[tabID];
            this.newPolicyKeys[tabID] = Math.random();
        }
    }
}
</script>

<style scoped>
.tab-content > .tab-pane {
    display: unset;
}

.fa {
    margin: 0 2px;
    cursor: pointer;
}

.policy-text {
    max-width: 100%;
    line-height: 1.65;
    white-space: normal;
    overflow-wrap: break-word;
}

/*
.table-bordered tr:first-child td {
    border-top: 0;
}
*/

.table-bordered tr:last-child td {
    border-bottom: 0;
}

.table-bordered tr > td:first-child {
    border-left: 0;
}

.table-bordered tr > td:last-child {
    border-right: 0;
}
</style>
<template>
    <div class="card">
        <div class="card-header">
            <h5>Geo-distributed Database Navigator</h5>
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
                  panels-wrapper-class="card-body tab-content">
                <tab v-for="tab in tabs" :key="tab.id" :name="tab.name" panel-class="tab-pane">
                    <database-navigator-relations :key="tab.id" :tab="tab.name" :databases="getRelationsForLocation(tab.name)" />
                </tab>
            </tabs>
        </div>
    </div>
</template>

<script>
import 'bootstrap/js/dist/tab';
import 'bootstrap/js/dist/collapse';
import { uniq } from 'lodash-es/array';
import { kebabCase } from 'lodash-es/string';
import {Tabs, Tab} from 'vue3-tabs-component';
import DatabaseNavigatorRelations from './DatabaseNavigatorRelations.vue';

export default {
    name: 'DatabaseNavigator',
    components: {
        Tab,
        Tabs,
        DatabaseNavigatorRelations
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
        getRelationsForLocation(location) {
            let relations = [];
            let schemas = this.configuration.schemas;
            if (location !== 'All') {
                schemas = schemas.filter(schema => schema.locations.includes(location));
            }
            schemas.forEach(schema => {
                relations.push({
                    id: schema.id,
                    name: schema.name,
                    relations: schema.relations
                });
            });
            relations.sort((r1, r2) => r1.name.localeCompare(r2.name));

            return relations;
        },
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
        getTabsFromSchemas(schemas) {
            return this.getLocations(schemas, true).map(location => {
                let kebabCaseLocation = kebabCase(location);

                return {
                    id: 'tab-' + kebabCaseLocation,
                    name: location
                };
            });
        }
    }
}
</script>

<style scoped>
.tab-content > .tab-pane {
    display: unset;
}
</style>
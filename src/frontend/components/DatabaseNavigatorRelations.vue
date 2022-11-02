<template>
    <ul class="nav pcoded-inner-navbar">
        <li v-for="database in databases" class="nav-item pcoded-hasmenu">
            <a @click.prevent :href="'#' + getCollapseName(database.name)" data-toggle="collapse" class="nav-link px-0 py-0">
                <span class="pcoded-micon"><i class="fa fa-database"></i></span>
                <span class="pcoded-mtext">{{ database.name }}</span>
            </a>
            <ul :id="getCollapseName(database.name)" class="pcoded-submenu collapse">
                <li v-for="relation in database.relations">
                    <a @click.prevent :href="'#' + getCollapseName(database.name + '-' + relation.name)" :data-type="relation.type" data-toggle="collapse">
                        <span class="pcoded-micon"><i class="fa fa-table"></i></span>
                        <span class="pcoded-mtext">{{ relation.name }}</span>
                    </a>
                    <div :id="getCollapseName(database.name + '-' + relation.name)" class="pcoded-submenu collapse">
                        <table class="table table-bordered table-sm">
                            <thead>
                                <tr>
                                    <th scope="col">Column</th>
                                    <th scope="col">Type</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="row in relation.structure">
                                    <th scope="row">{{ row.column }}</th>
                                    <td>{{ row.type }}</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </li>
            </ul>
        </li>
    </ul>
</template>

<script>
import { kebabCase } from 'lodash-es/string';

export default {
    name: 'DatabaseNavigatorRelations',
    props: {
        tab: String,
        databases: Array
    },
    methods: {
        getCollapseName(name) {
            return 'collapse-' + kebabCase(this.tab) + '-' + kebabCase(name);
        }
    }
}
</script>

<style scoped>
.nav.pcoded-inner-navbar {
    overflow-x: auto;
}
.nav {
    flex-direction: column;
}
.nav-item {
    padding: 10px 0;
}
.nav-item:first-child {
    padding-top: 0;
}
.nav-item:last-child {
    padding-bottom: 0;
}
.nav-link {
    display: inline-block;
}
.pcoded-mtext {
    margin-left: 0.5em;
}
.pcoded-submenu {
    list-style: none;
    margin: 10px 0;
    padding-left: 1.4em;
}
</style>
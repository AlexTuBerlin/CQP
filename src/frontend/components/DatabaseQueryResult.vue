<template>
    <div class="card" v-if="hasQueryResult">
        <div class="card-header">
            <h5>Query Result</h5>
        </div>

        <div class="card-block px-0 py-0">
            <div class="container">
                <div class="row px-2 py-2 small bg-text-primary">
                    <div class="col-md-auto px-0">
                        Showing 1 to {{ queryResultInformation.limit }} of {{ queryResultInformation.resultSize }} rows
                    </div>
                    <div class="col px-0 text-right" v-if="queryResultInformation.executionTime > 0">
                        Execution time: {{ queryResultInformation.executionTime }}ms
                    </div>
                </div>
            </div>
            <table class="table table-bordered table-sm mb-0" style="overflow-x: auto">
                <thead>
                    <tr>
                        <th scope="col" v-for="column in queryResult.columns">{{ column.name }}</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="row in queryResult.rows">
                        <td v-for="value in row">{{ value }}</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>

<script>
export default {
    name: 'DatabaseQueryResult',
    computed: {
        configuration() {
            return this.$store.getters.configuration
        },
        // ToDo: Improve following naming/structure
        queryResult() {
            return this.$store.getters.queryResult?.result
        },
        queryResultInformation() {
            return {
                limit: this.$store.getters.queryResult?.limit,
                resultSize: this.$store.getters.queryResult?.resultSize,
                executionTime: this.$store.getters.queryResult?.executionTime
            }
        },
        hasQueryResult() {
            return this.queryResult && 'columns' in this.queryResult && 'rows' in this.queryResult;
        }
    }
}
</script>

<style scoped>
table.table-bordered,
table.table-bordered thead th {
    border-top: 0;
}
.bg-text-primary {
    color: #ffffff;
    background-color: #04a9f5;
}
</style>
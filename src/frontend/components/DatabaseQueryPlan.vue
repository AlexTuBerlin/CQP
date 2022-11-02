<template>
    <div class="card">
        <div class="card-header">
            <h5>Query Plan and Execution</h5>
        </div>

        <div v-if="!configuration" class="card-block px-2 py-5 text-center">
            <span>Please load your configuration file</span>
        </div>

        <div v-if="configuration" class="card-block px-0 py-2">
            <google-organization-chart v-if="orgChartData.getNumberOfRows() > 0"
                                       :data="orgChartData"
                                       :key="orgChartData.toJSON()"
                                       :selectHandler="orgChartSelectHandler"
                                       class="p-x-0" />
            <div v-if="orgChartData.getNumberOfRows() > 0 && showExecute" class="btn-group-sm btn-block text-right p-t-10 p-r-10" role="group">
                <button @click="executeQuery" :class="{ 'btn-success': canExecuteQuery, 'btn-danger': !canExecuteQuery }" type="button" class="btn">Execute</button>
            </div>
            <div v-if="orgChartData.getNumberOfRows() === 0" class="card-block px-2 py-5 text-center">
                Please enter a query and select the desired optimization
            </div>
        </div>
    </div>
    <modal v-if="showModal" :key="modalText" :title="modalTitle" :text="modalText" @closeModal="closeModal" />
</template>

<script>
import $ from 'jquery';
import 'bootstrap/js/dist/popover';
import GoogleOrganizationChart from './GoogleOrganizationChart.vue';
import Modal from './Modal.vue';
import axios from 'axios';
import { get } from 'lodash-es/object';

google.charts.load('current', { packages: ['corechart'] });

export default {
    name: 'DatabaseQueryPlan',
    components: {
        GoogleOrganizationChart,
        Modal
    },
    data() {
        return {
            modalTitle: null,
            modalText: null,
            canExecuteQuery: false
        }
    },
    methods: {
        closeModal() {
            this.modalTitle = null;
            this.modalText = null;
        },
        communicationErrorAlert(res) {
            this.modalTitle = 'The following error response was received for the given query execution';
            this.modalText = (get(res, 'response.data.message')) ? res.response?.data?.message : res.message;
        },
        orgChartSelectHandler() {
            $('[data-toggle="popover"]').popover();
            $('[data-toggle="popover"], .query-plan-operator').on('click', function () {
                $('[data-toggle="popover"], .query-plan-operator').not(this).popover('hide');
            });
        },
        executeQuery() {
            const formData = new FormData();
            formData.set('query', this.$store.getters.query);
            formData.set('optimizationType', this.$store.getters.queryOptimization);

            axios.post('/api/databases/query/execute', formData).then(res => {
                let queryResult = res.data.data.query;
                this.$store.dispatch('setQueryResult', queryResult);
            }).catch(this.communicationErrorAlert);
        }
    },
    computed: {
        configuration() {
            return this.$store.getters.configuration
        },
        showModal() {
            return this.modalTitle && this.modalText;
        },
        showExecute() {
            return this.$store.getters.queryOptimization !== 'none';
        },
        queryPlan() {
            return this.$store.getters.queryPlan
        },
        orgChartData() {
            let data = new google.visualization.DataTable();
            data.addColumn('string', 'id');
            data.addColumn('string', 'parent');

            // Hide visible popovers on chart data update
            $('[data-toggle="popover"]').popover('hide');

            // Populate chart data
            let rowIndex, i;
            let orgChartData = this.queryPlan;
            for (i = 0; i < orgChartData.length; i++) {
                rowIndex = data.addRow([{
                    v: orgChartData[i].id,
                    f: orgChartData[i].violation ?
                        '<a class="f-14 query-plan-operator has-popover" tabindex="0" role="button" data-id="' + orgChartData[i].id + '" data-toggle="popover" data-placement="left" title="' + orgChartData[i].violation.title + '" data-content="' + orgChartData[i].violation.text + '">' + orgChartData[i].operator + '</a>' :
                        '<a class="f-14 query-plan-operator" tabindex="0" role="button" data-id="' + orgChartData[i].id + '" >' + orgChartData[i].operator + '</a>'
                }, orgChartData[i].parent]);

                data.setRowProperty(rowIndex, 'operator', orgChartData[i].operator);
                data.setRowProperty(rowIndex, 'hasViolation', !!orgChartData[i].violation);
            }

            // Mark violated operation(s)
            if (data.getNumberOfRows() > 0) {
                // Get all violated operator indexes
                let violatedOperationRowIndexes = data.getSortedRows(0).map(rowIndex => {
                    let hasViolation = data.getRowProperty(rowIndex, 'hasViolation');
                    return hasViolation ? rowIndex : -1;
                }).filter(row => row >= 0);

                // Set style of violated operation(s) if existing
                if (violatedOperationRowIndexes.length > 0) {
                    this.canExecuteQuery = false;
                    violatedOperationRowIndexes.forEach(violatedOperationRowIndex => {
                        data.setRowProperty(violatedOperationRowIndex, 'style', 'background-color: #e38493;');
                    });
                } else {
                    // Get all ship operator indexes
                    let shipOperatorRowIndexes = data.getSortedRows(0).map(rowIndex => {
                        let operator = data.getRowProperty(rowIndex, 'operator');
                        let isShipOperator = operator.toLowerCase().trim() === 'ship';

                        return isShipOperator ? rowIndex : -1;
                    }).filter(row => row >= 0);

                    // Set style of ship operation(s) if existing
                    if (shipOperatorRowIndexes.length > 0) {
                        this.canExecuteQuery = true;
                        shipOperatorRowIndexes.forEach(shipOperatorRowIndex => {
                            data.setRowProperty(shipOperatorRowIndex, 'style', 'color: #ffffff; background-color: #22c55e;');
                        });
                    }
                }
            }

            return data;
        }
    }
}
</script>

<style scoped>
.btn-success {
    color: #ffffff;
    border-color: #22c55e;
    background-color: #22c55e;
}
.btn-success:hover {
    border-color: #189e49;
    background-color: #189e49;
}
</style>
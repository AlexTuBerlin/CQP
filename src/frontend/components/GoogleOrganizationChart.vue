<template>
    <div ref="chart"></div>
</template>

<script>
google.charts.load('current', { packages: ['orgchart'] });

export default {
    name: 'GoogleOrganizationChart',
    props: {
        data: Object,
        selectHandler: Function
    },
    mounted() {
        google.charts.setOnLoadCallback(this.drawChart)
    },
    methods: {
        drawChart () {
            let chartElement = this.$refs.chart;

            // Initialize org chart
            let chart = new google.visualization.OrgChart(chartElement);

            // Set select event handler
            google.visualization.events.addListener(chart, 'select', this.selectHandler);

            // Draw org chart
            chart.draw(this.data, {
                allowHtml: true,
                size: 'medium',
                nodeClass: 'nodeClass',
                selectedNodeClass: 'selectedNodeClass'
            });
        }
    }
}
</script>

<style>
.google-visualization-orgchart-table {
    border-collapse: separate;
}
</style>
<template>
    <div class="pcoded-wrapper">
        <div class="pcoded-content">
            <div class="pcoded-inner-content">
                <div class="navbar navbar-light bg-dark">
                    <a class="d-inline-block align-content-center">Dashboard</a>
                </div>
                <div class="main-body">
                    <div class="page-wrapper">
                        <!-- [ Main Content ] start -->
                        <div class="row">

                            <!-- [ Database pane ] start -->
                            <div class="col-md-3 m-b-30">
                                <database-configuration />
                                <database-navigator />
                            </div>
                            <!-- [ Database pane ] end -->

                            <!--[ Constraint pane ] start-->
                            <div class="col-md-4">
                                <database-policies />
                            </div>
                            <!--[ Constraint pane ] end-->

                            <!--[ Query pane ] start-->
                            <div class="col-md-5">
                                <database-query />
                                <database-query-plan />
                                <database-query-result />
                            </div>
                            <!--[ Query pane ] end-->

                        </div>
                        <!-- [ Main Content ] end -->
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import DatabaseConfiguration from './DatabaseConfiguration.vue'
import DatabaseNavigator from './DatabaseNavigator.vue'
import DatabasePolicies from './DatabasePolicies.vue';
import DatabaseQuery from './DatabaseQuery.vue';
import DatabaseQueryPlan from './DatabaseQueryPlan.vue';
import DatabaseQueryResult from './DatabaseQueryResult.vue';

export default {
    name: 'Dashboard',
    components: {
        DatabaseConfiguration,
        DatabaseNavigator,
        DatabasePolicies,
        DatabaseQuery,
        DatabaseQueryPlan,
        DatabaseQueryResult
    },
    mounted () {
        setTimeout(() => this.$store.dispatch('setLoading', false), 400);
    }
}
</script>

<style scoped>

</style>
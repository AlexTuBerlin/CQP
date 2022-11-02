<template>
    <div class="modal fade" ref="modal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">{{ title }}</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    {{ text }}
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import $ from 'jquery';
import 'bootstrap/js/src/modal';

export default {
    name: 'Modal',
    props: {
        title: String,
        text: String
    },
    emits: [
        'openModal',
        'closeModal'
    ],
    mounted() {
        $(this.$refs.modal)
            .on('show.bs.modal', () => this.$emit('openModal'))
            .on('hidden.bs.modal', () => this.$emit('closeModal'))
            .modal();
    }
}
</script>

<style scoped>
.modal-body {
    white-space: pre-line;
}
</style>
<template>
    <div ref="editor"></div>
</template>

<script>
import CodeMirror from 'codemirror/lib/codemirror.js';
import 'codemirror/mode/javascript/javascript.js';
import 'codemirror/mode/sql/sql.js';
import 'codemirror/addon/hint/show-hint.js';
import 'codemirror/addon/hint/sql-hint.js';
import 'codemirror/addon/display/autorefresh.js';
import 'codemirror/addon/edit/matchbrackets.js';

export default {
    name: 'Editor',
    props: {
        modelValue: {
            type: String,
            default: ''
        },
        width: {
            type: [Number, String],
            default: '100%'
        },
        height: {
            type: [Number, String],
            default: 158
        }
    },
    emits: ['update:modelValue'],
    mounted() {
        const editor = CodeMirror(this.$refs.editor, {
            mode: 'text/x-mysql',
            tabSize: 4,
            indentWithTabs: true,
            smartIndent: true,
            lineNumbers: true,
            matchBrackets: true,
            autoRefresh: true,
            autofocus: false,
            value: this.modelValue,
            extraKeys: {'Ctrl-Space': 'autocomplete'}
        });
        editor.on('change', (instance) => this.$emit('update:modelValue', instance.getValue()));
        editor.setSize(this.width, this.height);

        CodeMirror.commands.autocomplete = (cm) => {
            CodeMirror.showHint(cm, CodeMirror.hint.sql, {
                tables: this.$store.getters.getTables
            });
        }
    }
}
</script>

<style>
@import '~codemirror/lib/codemirror.css';
@import '~codemirror/addon/hint/show-hint.css';

.CodeMirror {
    font-family: monospace;
    border: 1px solid #ddd;
    border-radius: 5px;
}

.CodeMirror pre.CodeMirror-line,
.CodeMirror pre.CodeMirror-line-like {
    padding-left: 8px;
}
</style>
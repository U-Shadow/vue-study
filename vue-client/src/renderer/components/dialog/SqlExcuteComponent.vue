<template>
  <div>
    <el-dialog :title="componentDialogTitle" :visible="componentDialogVisible" @close="doClose" @open="doOpen" center >
      <el-form :model="form" ref="ruleForm">
        <el-form-item label="数据源" :label-width="formLabelWidth" prop="data.datasource" :rules="rules.datasource">
      		<el-select v-model="form.data.datasource" placeholder="" style="width: 100%;">
      		  <el-option label="请选择数据源" value=""></el-option>
      		  <el-option v-for="(item,index) in datasourceTree" :key="index"
      		  	:label="item.data.sourceName" :value="item.data"></el-option>
      		</el-select>
        </el-form-item>
        <!--<el-form-item label="变量名" :label-width="formLabelWidth" prop="data.result" :rules="rules.result">
			    <el-input v-model="form.data.result"></el-input>
			  </el-form-item>-->
        <el-form-item label="sql语句" :label-width="formLabelWidth" prop="data.sql" :rules="rules.sql">
			    <el-input v-model="form.data.sql" type="textarea" :autosize="{ minRows: 4, maxRows: 20}"></el-input>
			  </el-form-item>
      </el-form>
      
      <span slot="footer" class="dialog-footer">
	  		<el-button type="primary" @click="confirm">确 定</el-button>
		    <el-button @click="cancel">取 消</el-button>
		  </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapState, mapMutations } from 'vuex'
import { cloneDeep } from 'lodash/fp'
export default {
	name: 'sqlExcute',
	props: {
    componentDialogTitle: '',
    componentDialogVisible: Boolean,
		dialogActionType: {
    	type: Boolean,
    	required: true
   	},
   	_nodeData: {
 			type: Object,
 			required: true
 		}
	},
	watch: {
    componentDialogVisible() {
		}
	},
	computed: {
		...mapState('flow', ['componentData', 'nodeData', 'processInfo']),
		...mapState('processStore', ['datasourceTree'])
  },
	data () {
		return {
	  	formLabelWidth: '130px',
	  	form: {
	  		nodeType: 'sqlExcute',
	  		data: {// 实际业务数据
	  			datasource: '',
	  			sql: '',
	  			result: ''
	  		}
	   	},
	  	rules: {
        datasource: [
          { required: true, message: '请选择数据源', trigger: 'change' }
        ],
        sql: [
          { required: true, message: '请输入sql语句', trigger: 'blur' }
        ],
        result: [
          { required: true, message: '请输入变量名称', trigger: 'blur' }
        ]
      }
		}
	},
	methods: {
		...mapMutations('flow', ['UPDATE_COMPONENT_DATA', 'UPDATE_PROCESS']),
    //关闭
    doClose() {
      this.$emit('visibleChange', false);
    },
    doOpen() {
      this.setFormData()
    },
    setFormData() {
    	Object.assign(this.$data, this.$options.data())
      if(this.componentDialogVisible === true) {
        let data = this.componentData[this._nodeData.id]
        if(data) {
        	// 拷贝数据,避免再度修改数据的时候存在修改引用store的数据的错误
          this.form = cloneDeep(data)
        }
        else {
          Object.assign(this.$data, this.$options.data())
//        this.$nextTick(() => {
//        	this.$refs.ruleForm.resetFields();
//        })
        }
      }
    },
    // 确定
    confirm() {
    	this.$refs.ruleForm.validate((valid) => {
        if (valid) {
		    	let id = this._nodeData.id
		  		this.UPDATE_COMPONENT_DATA({
		  			[id]: this.form
		  		})
		      this.$emit('visibleChange', false);
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    // 取消
    cancel() {
    	this.$emit('visibleChange', false);
    	Object.assign(this.$data, this.$options.data())
    }
	},
	mounted () {
    this.setFormData()
  }
}
</script>

<style scoped="scoped">
</style>
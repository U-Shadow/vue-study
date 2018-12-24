<template>
  <div>
    <el-dialog :title="componentDialogTitle" :visible="componentDialogVisible" @close="doClose" @open="doOpen" center >
      <el-form :model="form" ref="ruleForm">
        <el-form-item label="文件路径" :label-width="formLabelWidth" prop="data.filePath" :rules="rules.filePath">
			    <el-input v-model="form.data.filePath">
			    	<el-button slot="append" icon="el-icon-document" @click="selectFile"></el-button>
			    </el-input>
			  </el-form-item>
			  <el-form-item label="变量名称" :label-width="formLabelWidth" prop="data.resVariable" :rules="rules.resVariable">
			    <el-input v-model="form.data.resVariable"></el-input>
			  </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
	  		<el-button type="primary" @click="confirm">确 定</el-button>
		    <el-button @click="cancel">取 消</el-button>
		  </span>
    </el-dialog>
    
    <input v-show="false" ref="file_select" type="file" @change="change"/>
  </div>
</template>

<script>
import { mapState, mapMutations } from 'vuex'
import { cloneDeep } from 'lodash/fp'
//import fs from 'fs'
export default {
	name: 'end',
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
		...mapState('flow', ['componentData', 'processInfo', 'currentProcess']),
		...mapState('processStore', ['processTree'])
  },
	data () {
		return {
			activeIndex: '',
			processParamsShow: false,
			paramsOrEleVisible: false,
	  	formLabelWidth: '130px',
	  	form: {
	  		nodeType: 'fileInput',
	     	data: {
	     		fileData: '',
	     		filePath: '',
	     		resVariable: 'fileInputInfo1'
	     	}
	  	},
		  rules: {
		   	filePath: [
          { required: true, message: '请输入(选择)文件地址', trigger: 'blur' }
        ],
        resVariable: [
        	{ required: true, message: '请输入变量名称', trigger: 'blur' }
        ]
		  }
		}
	},
	methods: {
		...mapMutations('flow', ['UPDATE_COMPONENT_DATA', 'UPDATE_PROCESS']),
		selectFile() {
			this.$refs.file_select.click()
		},
		/**
     * 打开的文件改变时监听事件
     */
    change() {
    	// xml配置文件读取
    	this.form.data.filePath = this.$refs.file_select.files[0].path
    	/*var that = this
    	fs.readFile(this.form.data.filePath, {encoding: 'utf-8'}, function (err, data) {
		   	if (err) {
	       	return console.error(err);
		   	}
		   	that.form.data.fileData = data.toString();
		   	that.form.data[that.form.data.resVariable] = data.toString()
			})*/
    },
    //关闭
    doClose() {
      this.$emit('visibleChange', false);
    },
    //打开
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
//      	this.$nextTick(() => {
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
		    	// 判断当前变量名称是否存在
		    	let isExist = this.processInfo[this.currentProcess].v_k[this.form.data.resVariable];
		    	if(isExist && isExist !== id) {	// 已存在该变量名称
		    		this.cusMessage("该变量名称已存在", "warning");
		    		return false;
		    	}
		  		this.UPDATE_COMPONENT_DATA({
		  			[id]: this.form
		  		})
		      let keys = cloneDeep(this.processInfo[this.currentProcess].keys)
		      let v_k = cloneDeep(this.processInfo[this.currentProcess].v_k)
		      if((typeof keys) === 'string') {
		      	keys = {};
		      }
		      if((typeof v_k) === 'string') {
		      	v_k = {};
		      }
		      keys[id] = this.form.data.resVariable
		      v_k[this.form.data.resVariable] = id
		      let processId = this.currentProcess;
		      let processName = this.processTree[processId].processName;
		      this.UPDATE_PROCESS({
		      	[this.currentProcess]: {
		      		processId: processId,
    					processName: processName,
	    				keys: keys,	// 流程中节点对应变量名称对象, 节点id对应变量名称(如果一个节点会对应多个变量名称,节点后面一次加_a、_b...)
	  					v_k: v_k	// 流程中变量名称对应节点对象, 用于判断填写的变量名称是否存在
	    			}
		      })
	      } else {
          console.log('error submit!!');
          return false;
        }
	      this.$emit('visibleChange', false);
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

<style lang="scss">
	.border-1{
		border: solid 1px #D3D3D3;
		min-height: 100px;
		padding: 5px 8px;
		.li-list-item{
			cursor: default;
		}
		.active{
			border: solid 1px #D3D3D3;
			border-radius:5px;
		}
	}
</style>
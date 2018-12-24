<template>
  <div>
    <el-dialog :title="componentDialogTitle" :visible="componentDialogVisible" @close="doClose" @open="doOpen" center >
      <el-form :model="form" ref="ruleForm">
      	<el-form-item label="文件存在" :label-width="formLabelWidth" prop="data.outputWay" :rules="rules.outputWay">
			    <el-select v-model="form.data.outputWay">
      		  <el-option label="覆盖" value="Override"></el-option>
      		  <el-option label="追加" value="Append"></el-option>
      		  <!--<el-option label="新建(加时间戳)" value="newFile"></el-option>-->
      		</el-select>
			  </el-form-item>
        <el-form-item label="文件路径" :label-width="formLabelWidth" prop="data.filePath" :rules="rules.filePath">
			    <el-input v-model="form.data.filePath">
			    	<!--<el-button slot="append" icon="el-icon-document" @click="selectFile"></el-button>-->
			    </el-input>
			  </el-form-item>
			  <el-row style="margin-bottom: 5px;">
				  <el-form-item label="输出内容" :label-width="formLabelWidth" style="width: 95%; float: left;">
				    <el-input v-model="form.data.output" type="textarea" :autosize="{ minRows: 4, maxRows: 20}"></el-input>
				  </el-form-item>
      		<el-button type="primary" size="mini" @click="addOutput" style="float: right;">变量</el-button>
      	</el-row>
			  <el-form-item label="编码" :label-width="formLabelWidth" prop="data.encoding" :rules="rules.encoding">
			    <el-select v-model="form.data.encoding">
      		  <el-option label="GBK" value="GBK"></el-option>
      		  <el-option label="UTF-8" value="UTF-8"></el-option>
      		  <el-option label="ISO-8859-1" value="ISO-8859-1"></el-option>
      		</el-select>
			  </el-form-item>
      </el-form>
      
      <span slot="footer" class="dialog-footer">
	  		<el-button type="primary" @click="confirm">确 定</el-button>
		    <el-button @click="cancel">取 消</el-button>
		  </span>
    </el-dialog>
    
    <input v-show="false" ref="file_select" type="file" @change="change"/>
    
    <el-dialog title="选择变量或元素" class="endDialog" :visible="paramsOrEleVisible" width="500px" left>
    	<!--<ul>
    		<li v-for="(item, index) in processInfo[currentProcess].keys" :key="index">{{item}}</li>
    	</ul>-->
    	<div class="flt-container border-1">
	    	<ul>
	      	<li class="flt-tool-list">
	          <div class="flt-list-tit" @click.stop="processParamsShow = !processParamsShow">
	            <icon :name="processParamsShow?'down-arrow':'up-arrow'" :size="8" 
	            	class="flt-icon" style="vertical-align: 0em;"></icon>
	            <span>流程变量</span>
	          </div>
	          <div class="flt-shape-list" v-show="processParamsShow">
	            <div class="flt-item-box">
	            	<div :class="activeIndex === index ? 'li-list-item active ': 'li-list-item'" 
	            		v-for="(item,index) in processInfo[currentProcess].keys" :key="index"
	          			@click="selected(index)" @dblclick="confirmParamsOrEle(index)">
	          			<span style="margin-left: 10px;">{{item}}</span>
	              </div>
	            </div>
	          </div>
	        </li>
	     	</ul>
	    </div>
    	
      <span slot="footer" class="dialog-footer">
	  		<el-button type="primary" @click="confirmParamsOrEle(activeIndex)">确 定</el-button>
		    <el-button @click="cancelParamsOrEle">取 消</el-button>
		  </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapState, mapMutations } from 'vuex'
import { cloneDeep } from 'lodash/fp'
//import fs from 'fs'
export default {
	name: 'fileOutput',
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
		...mapState('flow', ['componentData', 'processInfo', 'currentProcess'])
  },
	data () {
		return {
			activeIndex: '',
			processParamsShow: false,
			paramsOrEleVisible: false,
	  	formLabelWidth: '130px',
	  	form: {
	  		nodeType: 'fileOutput',
	     	data: {
	     		outputWay: 'Override',
	     		encoding: 'UTF-8',
	     		filePath: '',
	     		output: ''
	     	}
	   	},
	   	rules: {
		   	filePath: [
          { required: true, message: '请输入文件地址', trigger: 'blur' }
        ]
		  }
		}
	},
	methods: {
		...mapMutations('flow', ['UPDATE_COMPONENT_DATA']),
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
		selected(index) {
			this.activeIndex = index
		},
		confirmParamsOrEle(index) {
			this.form.data.output = this.form.data.output + '{(' + this.processInfo[this.currentProcess].keys[index] + ')}'
			this.paramsOrEleVisible = false
		},
		cancelParamsOrEle() {
			this.paramsOrEleVisible = false
		},
    // 添加输出内容
    addOutput() {
  		this.paramsOrEleVisible = true
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
    this.setFormData();
  }
}
</script>

<style lang="scss">
	/*.border-1{
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
	}*/
</style>
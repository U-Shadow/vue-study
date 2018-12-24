<template>
  <div>
    <el-dialog :title="componentDialogTitle" :visible="componentDialogVisible" @close="doClose" @open="doOpen" center >
      <el-form :model="form" ref="ruleForm">
        <el-form-item label="服务地址" :label-width="formLabelWidth" prop="data.address" :rules="rules.address">
			    <el-input v-model="form.data.address"></el-input>
			  </el-form-item>
			  <!--<el-row v-for="(item, index) in form.httpParams" :key="index">
	      	<el-form-item label="参数名" :label-width="formLabelWidth" class="http_param">
	        	<el-input v-model="item.k"></el-input>
	        </el-form-item>
	        <el-form-item label="参数值" :label-width="formLabelWidth" class="http_param">
	        	<el-input v-model="item.v"></el-input>
	        </el-form-item>
	        <el-button type="primary" size="mini" icon="el-icon-plus" circle 
	        	 v-if="index === (form.httpParams.length-1)" @click="addParam"></el-button>
	        <el-button type="primary" size="mini" icon="el-icon-minus" circle 
	        	 v-if="index === (form.httpParams.length-1) && index !== 0" @click="minusParam"></el-button>
      	</el-row>-->
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
	name: 'jetty',
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
		...mapState('flow', ['componentData', 'nodeData'])
  },
	data () {
		return {
	  	formLabelWidth: '130px',
	  	form: {
		  	httpParams: [{
	  			k: '',
	  			v: ''
	  		}],
	  		nodeType: 'jetty',
	  		data: {
	  			address: 'http://'
//	  			paramsData: {}
	  		}
	  	},
	  	beanData: '',
	  	camelData: '',
	  	rules: {
        address: [
          { required: true, message: '请输入正确服务地址', trigger: 'blur' }
        ]
      }
		}
	},
	methods: {
		...mapMutations('flow', ['UPDATE_COMPONENT_DATA']),
    /**
		 * 添加http参数
		 */
    addParam() {
    	this.form.httpParams.push({
    		k: '',
    		v: ''
    	})
    },
    minusParam(index) {
    	this.form.httpParams.pop()
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
		    	// 将添加的参数加入到form中
//		    	this.form.data.paramsData = {}	// 先置空,清除原有数据
//		    	for(var i=0; i<this.form.httpParams.length; i++) {
//		    		let k = this.form.httpParams[i].k
//		    		let v = this.form.httpParams[i].v
//		    		this.form.data.paramsData[k] = v
//		    	}
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
	.cus_footer{
		margin-top: 30px;
		text-align: center;
	}
	.http_address {
		width: 89%;
	}
	.http_method {
		width: 10%
	}
	.http_param {
		width: 45%;
		display: inline-block;
	}
</style>
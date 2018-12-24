<template>
  <div>
    <el-dialog :title="componentDialogTitle" :visible="componentDialogVisible" @close="doClose" @open="doOpen" center >
      <el-form :model="form" ref="ruleForm" label-position="left">
      	<el-form-item label="源队列名称" :label-width="formLabelWidth" prop="data.queueName" :rules="rules.queueName">
        	<el-input v-model="form.data.queueName"></el-input>
        </el-form-item>
        <el-form-item label="上下文工厂" :label-width="formLabelWidth">
        	<el-input v-model="form.data.factory"></el-input>
        </el-form-item>
        <el-form-item label="JNDI位置绑定" :label-width="formLabelWidth">
        	<el-input v-model="form.data.jndiAddress"></el-input>
        </el-form-item>
        <!--<el-form-item label="连接工厂" :label-width="formLabelWidth">
        	<el-input v-model="form.data.connFactory" :disabled="true"></el-input>
        </el-form-item>-->
      	<el-checkbox v-model="form.anonymousConn">匿名连接</el-checkbox>
        <el-form-item label="用户名" :label-width="formLabelWidth">
        	<el-input v-model="form.data.userName" :disabled="form.anonymousConn"></el-input>
        </el-form-item>
        <el-form-item label="密码" :label-width="formLabelWidth">
        	<el-input v-model="form.data.password" :disabled="form.anonymousConn"></el-input>
        </el-form-item>
       <!-- <el-form-item label="消息头变量名:" :label-width="formLabelWidth">
        	<el-input v-model="form.data.userName"></el-input>
        </el-form-item>-->
        <el-form-item label="消息内容变量名" :label-width="formLabelWidth" prop="data.msgDataName" :rules="rules.msgDataName">
        	<el-input v-model="form.data.msgDataName"></el-input>
        </el-form-item>
        <el-form-item label="消息筛选规则" :label-width="formLabelWidth">
        	<el-input v-model="form.data.filter"></el-input>
        </el-form-item>
        <el-form-item label="请求超时" :label-width="formLabelWidth">
        	<el-input v-model="form.data.timeout"></el-input>
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
	name: 'JMSFetch',
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
	  	formLabelWidth: '130px',
	  	form: {
	  		nodeType: 'JMSFetch',
	  		anonymousConn: true,
	  		data: {// 实际业务数据
	  			queueName: '',
	  			factory: 'org.apache.activemq.ActiveMQConnectionFactory',
	  			jndiAddress: 'tcp://localhost:61616',
	  			connFactory: '',
	  			userName: '',
	  			password: '',
	  			output: '',
	  			msgDataName: 'JMSFetchData1',
	  			filter: '',
	  			timeout: 10
	  		}
	   	},
	   	rules: {
        queueName: [
          { required: true, message: '请输入队列名称', trigger: 'blur' }
        ],
        msgDataName: [
        	{ required: true, message: '请输入消息内容变量名', ttigger: 'blur' }
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
		    	let isExist = this.processInfo[this.currentProcess].v_k[this.form.data.msgDataName];
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
		      keys[id] = this.form.data.msgDataName
		      v_k[this.form.data.msgDataName] = id
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
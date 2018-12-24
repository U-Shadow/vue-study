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
	name: 'JMSInput',
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
		...mapState('flow', ['componentData'])
  },
	data () {
		return {
	  	formLabelWidth: '130px',
	  	form: {
	  		anonymousConn: true,
	  		nodeType: 'JMSInput',
	  		data: {// 实际业务数据
	  			queueName: '',
	  			factory: 'org.apache.activemq.ActiveMQConnectionFactory',
	  			jndiAddress: 'tcp://localhost:61616',
	  			connFactory: '',
	  			userName: '',
	  			password: '',
	  			output: ''
	  		}
	   	},
	   	rules: {
        queueName: [
          { required: true, message: '请输入队列名称', trigger: 'blur' }
        ]
      }
		}
	},
	methods: {
		...mapMutations('flow', ['UPDATE_COMPONENT_DATA']),
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
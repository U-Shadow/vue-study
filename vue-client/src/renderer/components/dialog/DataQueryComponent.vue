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
        <el-form-item label="变量名" :label-width="formLabelWidth" prop="data.result" :rules="rules.result">
			    <el-input v-model="form.data.result"></el-input>
			  </el-form-item>
        <el-form-item label="sql语句" :label-width="formLabelWidth" prop="data.sql" :rules="rules.sql"
        		style="width: 97%; float: left;">
			    <el-input v-model="form.data.sql" type="textarea" :autosize="{ minRows: 4, maxRows: 20}"></el-input>
			  </el-form-item>
			  <el-button type="primary" plain size="mini" :disabled="form.data.datasource === '' ? true : false"
			  		@click="selectTable" style="float: right;" icon="el-icon-news"></el-button>
      </el-form>
      
      <span slot="footer" class="dialog-footer">
	  		<el-button type="primary" @click="confirm">确 定</el-button>
		    <el-button @click="cancel">取 消</el-button>
		  </span>
    </el-dialog>
    
    <el-dialog title="选择表" class="endDialog" :visible="paramsOrEleVisible" width="500px" left>
    	<!--<ul>
    		<li v-for="(item, index) in processInfo[currentProcess].keys" :key="index">{{item}}</li>
    	</ul>-->
    	<div class="flt-container border-1">
  			<label style="width: 100%;">选择表</label>
	    	<ul>
	      	<li class="flt-tool-list">
            <div class="flt-item-box">
            	<div style="padding: 2px 0;" :class="activeIndex === index ? 'li-list-item active ': 'li-list-item'" 
            		v-for="(item,index) in tables" :key="index"
          			@click="selected(index, item.table_name)" @dblclick="confirmParamsOrEle(index)">
          			<span style="margin-left: 10px;">{{item.table_name}}</span>
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
export default {
	name: 'dataQuery',
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
		...mapState('flow', ['componentData', 'nodeData', 'processInfo', 'currentProcess']),
		...mapState('processStore', ['datasourceTree', 'processTree'])
  },
	data () {
		return {
			activeIndex: '',
			paramsOrEleVisible: false,
	  	formLabelWidth: '130px',
	  	tables: [],
	  	form: {
	  		nodeType: 'dataQuery',
	  		data: {// 实际业务数据
	  			datasource: '',
	  			sql: '',
	  			result: 'dataQueryData1'
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
		selected(index) {
			this.activeIndex = index
		},
		confirmParamsOrEle(index) {
			this.paramsOrEleVisible = true;
			var dbInfo = this.form.data.datasource;
			var connection = this.connectMysql(dbInfo);
			this.queryAllCol(connection, dbInfo.database, this.tables[index].table_name);
//			this.paramsOrEleVisible = false
		},
		cancelParamsOrEle() {
			this.paramsOrEleVisible = false
		},
		selectTable() {
			this.paramsOrEleVisible = true;
			var dbInfo = this.form.data.datasource;
			var connection = this.connectMysql(dbInfo);
			this.queryAllTabel(connection, dbInfo.database);
		},
		// 查询所有表
		queryAllTabel(connection, database) {
			var sql = "select table_name from information_schema.tables where table_schema='" + database + "'"
			connection.query(sql, (error, results, fields) => {
        if (error) {
        	throw error
        }
        this.tables = results;
        this.closeMysql(connection);
     	})
		},
		// 查询表的所有列
		queryAllCol(connection, database, tableName) {
			var sql = "select COLUMN_NAME from information_schema.COLUMNS where table_schema='"
				+ database + "' and table_name = '"
				+ tableName + "'"
			connection.query(sql, (error, results, fields) => {
        if (error) {
        	throw error
        }
       	let cols = "";
       	if(results && results.length > 0) {
       		for(var i=0; i<results.length; i++) {
       			if(i != results.length-1) {
       				cols += "\t" + results[i].COLUMN_NAME + "," + this.$newLine
       			}
       			else{
       				cols += "\t" + results[i].COLUMN_NAME + this.$newLine
       			}
       		}
       	}
       	this.form.data.sql = "select " + this.$newLine + cols + "from " + tableName
        this.closeMysql(connection);
        this.paramsOrEleVisible = false
     	})
		},
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
		    	// 判断当前变量名称是否存在
		    	let isExist = this.processInfo[this.currentProcess].v_k[this.form.data.result];
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
		      keys[id] = this.form.data.result
		      v_k[this.form.data.result] = id
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
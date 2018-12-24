var express = require('express')
var app = express()
//var fs = require('fs')
var file = require('./file.js')

// 跨域请求问题
app.use('*', function (req, res, next) {
  res.header('Access-Control-Allow-Origin', '*');
  res.header('Access-Control-Allow-Headers', 'Content-Type,Content-Length, Authorization, Accept,X-Requested-With');
  res.header('Access-Control-Allow-Methods', 'PUT,POST,GET,DELETE,OPTIONS');
  if (req.method === 'OPTIONS') {
    res.send(200)
  } else {
    next()
  }
})

//app.get('/listUsers', function (req, res) {
//	fs.readFile(__dirname + '/' + 'users.json', 'utf8', function (err, data) {
//	console.log(data)
//	res.end(data)
//})
//})

app.post('/writeFile', function (req, res) {
	let path = file.writeFile(req.query)
	res.end(path)
})

/**
 * 同步读取文件
 */
app.post('/readFileSync', function (req, res) {
	let data = file.readFileSync(req.query.path)
	res.end(data)
})

/**
 * 异步读取文件
 */
app.post('/readFile', function (req, res) {
	let data = file.readFile(req.query)
	console.log('===', data)
	res.end(data)
})

var server = app.listen(9999, function () {
	var host = server.address().address
	var port = server.address().port
	console.log('应用实例，访问地址为 http://%s:%s', host, port)
	console.log('     ')
})

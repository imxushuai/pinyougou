app.service('uploadService', function ($http) {

    // 上传文件
    this.uploadFile = function () {
        // FormData对象，对数据进行二进制序列化
        var formData = new FormData();
        // 将文件转换为二进制数据进行传输
        formData.append('file', file.files[0]);

        /*
         必须设置Content-Type为undefined
         transformRequest:angular.identity 对表单进行序列化
          */
        return $http({
            url:'../upload.do',
            method:'post',
            data:formData,
            headers:{'Content-Type':undefined},
            transformRequest:angular.identity
        });
    }
});
/**
 * Filesize Filter
 * @Param length, default is 0
 * @return string
 *
 * https://gist.github.com/yrezgui/5653591
 */
angular.module('filters.filesize', [])
    .filter('filesize', function () {
        var units = [
            'bytes',
            'KB',
            'MB',
            'GB',
            'TB',
            'PB'
        ];

        return function (bytes, precision) {
            if (isNaN(parseFloat(bytes)) || !isFinite(bytes)) {
                return '?';
            }

            var unit = 0;

            while (bytes >= 1024) {
                bytes /= 1024;
                unit++;
            }

            return bytes.toFixed(+precision) + ' ' + units[ unit ];
        };
    });

/**
 * Usage
 * var myFile = 5678;
 *
 * {{myText|filesize}}
 *
 * Output
 * "5.54 Kb"
 *
 */
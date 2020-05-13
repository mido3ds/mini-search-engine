const webpack = require("webpack");
const path = require("path");
const fs_extra = require("fs-extra");

module.exports = env => ({
  mode: 'development',
  devtool: 'source-map',
  entry: {
    index: path.join(__dirname, 'src/main/js/pages/index/index.js'),
    search: path.join(__dirname, 'src/main/js/pages/search/index.js'),
    images: path.join(__dirname, 'src/main/js/pages/images/index.js'),
    trends: path.join(__dirname, 'src/main/js/pages/trends/index.js'),
  },
  output: {
    path: path.join(__dirname, 'src/main/resources/static/built'),
    filename: '[name].js'
  },
  devServer: {
    publicPath: '/built',
    contentBase: path.join(__dirname, 'src/main/resources/templates'),
    port: 8081,
    hot: true,
    after: (app, server, compiler) => {
      fs_extra.copySync(path.join(__dirname, 'src/main/resources/static/built'), path.join(__dirname, 'built'))
    },
    historyApiFallback: {
      rewrites: [
        { from: /^\/$/, to: '/index.html' },
        { from: /search/, to: '/search.html' },
        { from: /images/, to: '/images.html' },
        { from: /trends/, to: '/trends.html' },
      ]
    }
  },
  resolve: {
    extensions: ['.js', '.jsx', '.ts', '.tsx']
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        use: 'babel-loader'
      },
      { test: /\.tsx?$/, loader: "ts-loader" },
      {
        test: /\.css$/i,
        use: ['style-loader', 'css-loader'],
      }
    ]
  },
  plugins: [
    new webpack.DefinePlugin({
      "process.env.BASE_PATH": '"' + env.BASE_PATH + '"'
    })
  ]
});

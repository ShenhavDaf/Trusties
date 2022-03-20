const Post = require("../Models/post_model");
const User = require("../Models/user_model");
const Sos = require("../Models/sos_model");
const Comment = require("../Models/comment_model");

const getPosts = async (req, res, next) => {
  Post.find({ role: "QUESTION" }, function (err, docs) {
    if (err) {
      console.log(err);
    } else {
      res.status(200).send(docs);
    }
  });
};

const getPostsById = async (req, res, next) => {
  try {
    posts = await Post.findById(req.params.id);
    res.status(200).send(posts);
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

const addPosts = async (req, res, next) => {
  var email = req.body.email;
  const user = await User.findOne({ email: email });
  var description = req.body.description;
  var title = req.body.title;
  var time = req.body.time;
  var role = req.body.role;

  const post = await Post({
    sender: user,
    title: title,
    time: time,
    description: description,
    role: role,
  });

  // const post = Post({
  //     message: req.body.message,
  //     sender: sender
  // });

  post.save((error, newPost) => {
    if (error) {
      console.log("error");
      res.status(400).send({
        status: "fail",
        error: error.message,
      });
      console.log(error.message);
    } else {
      console.log("post added!");
      res.status(200).send({
        status: "OK",
        post: newPost,
      });
    }
  });
};

const editPost = async (req, res, next) => {
  try {
    const exists = await Post.updateOne(
      { id: req.params.id },
      {
        title: req.body.title,
        description: req.body.description,
      }
    );
    if (exists == null) return sendError(res, 400, "post does not exist");
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

const deletePost = async (req, res, next) => {
  try {
    const exists = await Post.deleteOne({ _id: req.body.id });
    if (exists == null) return sendError(res, 400, "post does not exist");
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

const getMyPosts = async (req, res, next) => {
  // User.find({ id: req.user.id }, function (err, docs) {
  //     if (err) {
  //         console.log(err);
  //     }
  //     else {
  //         Post.find({ sender: docs }, function (err, docs_post) {
  //             if (err) {
  //                 console.log(err);
  //             }
  //             else {
  //                 res.status(200).send(docs_post);
  //             }
  //         });
  //     }
  // });
};

module.exports = {
  getPosts,
  getPostsById,
  addPosts,
  getMyPosts,
  editPost,
  deletePost,
};

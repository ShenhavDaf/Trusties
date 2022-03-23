const User = require("../Models/user_model");
const Post = require("../Models/post_model");
const Sos = require("../Models/sos_model");
const Comment = require("../Models/comment_model");

const addComment = async (req, res, next) => {
  console.log("add new comment ");

  const user = await User.findOne({ email: req.body.email });
  const post = await Post.findOne({ _id: req.body.postID });
  const message = req.body.message;
  const time = req.body.time;

  const comment = Comment({
    sender: user,
    post: post,
    message: message,
    time: time,
  });

  comment.save((error, comment) => {
    if (error) {
      res.status(400).send({
        status: "fail",
        error: error.message,
      });
    } else {
      res.status(200).send({
        status: "OK",
        comment: comment,
      });
    }
  });
};

const getAllComments_Post = async (req, res, next) => {
  // try {
  //     posts = await Post.where(p => p.role == "QUES").find()
  //     res.status(200).send(posts);
  // } catch (err) {
  //     res.status(400).send({
  //         'status': 'fail',
  //         'error': error.message
  //     });
  // }
};

const editComment = async (req, res, next) => {
  // console.log('add new comment ');
  try {
    const exists = await Comment.updateOne(
      { _id: req.params.id },
      {
        message: req.body.message,
      }
    );
    if (exists == null) return sendError(res, 400, "comment does not exist");
    else {
      console.log("comment edited!");
      res.status(200).send({
        status: "OK",
        post: newPost,
      });
    }
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

const deleteComment = async (req, res, next) => {
  try {
    const exists = await Comment.updateOne(
      { _id: req.params.id },
      {
        isDeleted: true,
      }
    );
    if (exists == null) return sendError(res, 400, "comment does not exist");
    else {
      console.log("comment deleted!");
      res.status(200).send({
        status: "OK",
        post: newPost,
      });
    }
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

module.exports = {
  addComment,
  editComment,
  deleteComment,
  getAllComments_Post,
};

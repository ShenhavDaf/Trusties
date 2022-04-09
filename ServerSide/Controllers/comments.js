const User = require("../Models/user_model");
const Post = require("../Models/post_model");
const Sos = require("../Models/sos_model");
const Comment = require("../Models/comment_model");

const getAllComments = async (req, res, next) => {
  Comment.find({}, function (err, docs) {
    if (err) console.log(err);
    else res.status(200).send(docs);
  });
};

const getPostComments = async (req, res, next) => {
  Post.findById(req.params.id)
    .populate({ path: "comments" })
    .exec((err, postIncludeComments) => {
      res.status(200).send(postIncludeComments.comments);
    });
};

const getCommentById = async (req, res, next) => {
  try {
    const comment = await Comment.findById(req.params.id);
    res.status(200).send(comment);
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};
const addComment = async (req, res, next) => {
  console.log("add new comment ");

  const user = await User.findOne({ email: req.body.sender });
  const post = await Post.findOne({ _id: req.body.postId });
  const message = req.body.content;
  const time = req.body.currentTime;

  const comment = Comment({
    sender: user,
    post: post,
    message: message,
    time: Date.now(),
  });

  comment.save(async (error, comment) => {
    if (error) {
      res.status(400).send({
        status: "fail",
        error: error.message,
      });
    } else {
      await Post.updateOne(
        { _id: req.body.postId },
        {
          $push: { comments: comment._id },
        }
      );

      console.log("Comment Saved");
      res.status(200).send({
        status: "OK",
        comment: comment,
      });
    }
  });
};

const editComment = async (req, res, next) => {
  // console.log("Edit Comment");
  // console.log("req.params.id");
  // console.log(req.params.id);
  // console.log("req.params.comtent");
  // console.log(req.body.content);

  try {
    const exists = await Comment.updateOne(
      { _id: req.params.id },
      {
        message: req.body.content,
      }
    );
    const updateComment = await Comment.findById(req.params.id);
    if (exists == null) return sendError(res, 400, "comment does not exist");
    else {
      console.log("comment edited!");
      res.status(200).send({
        status: "OK",
        comment: updateComment,
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
      const currComment = await Comment.findById(req.params.id);
      const currPost = await Post.findById(currComment.post);

      Post.updateOne(
        { _id: currPost.id },
        { $pull: { comments: req.params.id } },
        (err) => {
          if (err) console.log(err);
          else console.log("comment deleted!");
        }
      );

      res.status(200).send({
        status: "OK",
        comment: currComment,
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
  getAllComments,
  getPostComments,
  getCommentById,
  addComment,
  editComment,
  deleteComment,
};

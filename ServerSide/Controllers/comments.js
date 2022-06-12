const User = require("../Models/user_model");
const Post = require("../Models/post_model");
const Sos = require("../Models/sos_model");
const Comment = require("../Models/comment_model");
const { object } = require("mongoose/lib/utils");

// const getAllComments = async (req, res, next) => {
//   Comment.find({}, function (err, docs) {
//     if (err) console.log(err);
//     else res.status(200).send(docs);
//   });
// };

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
    if (comment != null) res.status(200).send(comment);
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};
const addComment = async (req, res, next) => {
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

      res.status(200).send({
        status: "OK",
        comment: comment,
      });
    }
  });
};

const editComment = async (req, res, next) => {
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

//## the function increase the user rating value,
//## insert the user to comment.report_positive list,
//## if the user is the post sender, comment.IsCorrect become true

const negativeComment = async (req, res, next) => {
  try {
    const reporter = await User.findById(req.body.user_rate);
    if (!reporter) {
      res.status(400).send({
        status: "fail",
        error: "User not found!",
      });
    }

    Comment.findById({ _id: req.params.id })
      .populate({
        path: "post",
        model: "Post",
        populate: {
          path: "sender",
          model: "User",
        },
      })
      .populate({
        path: "report_positive",
        model: "User",
      })
      .populate({
        path: "report_negative",
        model: "User",
      })
      .populate({
        path: "sender",
        model: "User",
      })
      .exec(function (err, comment) {
        if (err) {
          res.status(400).send({
            status: "fail",
            error: err.message,
          });
        }

        //Checking if the reporter already report positive
        for (var i = 0; i < comment.report_positive.length; i++) {
          if (comment.report_positive[i].email == reporter.email) {
            comment.report_positive.splice(i, 1);
          }
        }

        comment.report_negative.addToSet(reporter._id);
        //If user reporter is the post sender-> IsCorrect:true
        if (reporter.email == comment.post.sender.email) {
          comment.isCorrect = false;
          // comment.post.status = "OPEN";
        }

        comment.save(function (err) {
          if (err) {
            res.status(400).send({
              status: "fail",
              error: err.message,
            });
          }
        });

        if (comment.sender.rating * 0.9 < 0) {
          comment.sender.rating = 0;
        } else {
          comment.sender.rating = comment.sender.rating * 0.9;
        }

        comment.sender.numberReviews += 1;

        comment.sender.save(function (err) {
          if (err) {
            res.status(400).send({
              status: "fail",
              error: err.message,
            });
          }
        });

        comment.post.save(function (err) {
          if (err) {
            res.status(400).send({
              status: "fail",
              error: err.message,
            });
          }
        });
        res.status(200).send({
          status: "OK",
        });
      });
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

const positiveComment = async (req, res, next) => {
  try {
    const reporter = await User.findById(req.body.user_rate);
    if (!reporter) {
      res.status(400).send({
        status: "fail",
        error: "User not found!",
      });
    }

    Comment.findById({ _id: req.params.id })
      .populate({
        path: "post",
        model: "Post",
        populate: {
          path: "sender",
          model: "User",
        },
      })
      .populate({
        path: "report_positive",
        model: "User",
      })
      .populate({
        path: "report_negative",
        model: "User",
      })
      .populate({
        path: "sender",
        model: "User",
      })
      .exec(function (err, comment) {
        if (err) {
          res.status(400).send({
            status: "fail",
            error: err.message,
          });
        }

        //Checking if the reporter already report positive
        for (var i = 0; i < comment.report_negative.length; i++) {
          if (comment.report_negative[i].email == reporter.email) {
            comment.report_negative.splice(i, 1);
          }
        }

        comment.report_positive.addToSet(reporter._id);
        if (reporter.email == comment.post.sender.email) {
          comment.isCorrect = true;
          // comment.post.status = "CLOSE";
        }
        comment.save(function (err) {
          if (err) {
            res.status(400).send({
              status: "fail",
              error: err.message,
            });
          }
        });

        if (comment.sender.rating * 1.1 > 5) {
          comment.sender.rating = 5;
        } else if (comment.sender.rating == 0) {
          comment.sender.rating += 0.1;
        } else {
          comment.sender.rating = comment.sender.rating * 1.1;
        }

        comment.sender.numberReviews += 1;

        comment.sender.save(function (err) {
          if (err) {
            res.status(400).send({
              status: "fail",
              error: err.message,
            });
          }
        });

        comment.post.save(function (err) {
          if (err) {
            res.status(400).send({
              status: "fail",
              error: err.message,
            });
          }
        });
        res.status(200).send({
          status: "OK",
        });
      });
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

module.exports = {
  // getAllComments,
  getPostComments,
  getCommentById,
  addComment,
  editComment,
  deleteComment,
  positiveComment,
  negativeComment,
  // getCommentRate,
};

const Post = require("../Models/post_model");
const User = require("../Models/user_model");
const Sos = require("../Models/sos_model");
const Comment = require("../Models/comment_model");

const UserController = require("../Controllers/users");
const { route } = require("../Routes/post_routes");
const router = require("../Routes/post_routes");

const getAllPosts = async (req, res, next) => {
  Post.find({}, function (err, docs) {
    if (err) console.log(err);
    else res.status(200).send(docs);
  });
};

/* ********************************************************** */

// const getQuestions = async (req, res, next) => {
//   Post.find({ role: "QUESTION" }, function (err, docs) {
//     if (err) console.log(err);
//     else res.status(200).send(docs);
//   });
// };

// const getSOSs = async (req, res, next) => {
//   Post.find({ role: "SOS" }, function (err, docs) {
//     if (err) console.log(err);
//     else res.status(200).send(docs);
//   });
// };

const getPostsById = async (req, res, next) => {
  try {
    const posts = await Post.findById(req.params.id);

    res.status(200).send(posts);
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

/* ********************************************************** */

const addPosts = async (req, res, next) => {
  var email = req.body.email;
  const user = await User.findOne({ email: email });
  var description = req.body.description;
  var title = req.body.title;
  var time = req.body.time;
  var role = req.body.role;
  var category = req.body.category;
  var photo = req.body.photo;

  console.log("circle = " + req.body.circle);

  var friendsCircle;
  if (role == "SOS") friendsCircle = Number(req.body.circle);
  // else friendsCircle = 1;

  const post = await Post({
    sender: user,
    title: title,
    time: time,
    description: description,
    role: role,
    friends_circle: friendsCircle,
    category: category,
    photo: photo,
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

/* ********************************************************** */

const editPost = async (req, res, next) => {
  try {
    const exists = await Post.updateOne(
      { _id: req.params.id },
      {
        title: req.body.title,
        description: req.body.description,
      }
    );
    const updatePost = await Post.findById(req.params.id);
    if (exists == null) return sendError(res, 400, "post does not exist");
    else {
      console.log("post edited!");
      res.status(200).send({
        status: "OK",
        post: updatePost,
      });
    }
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

/* ********************************************************** */

const deletePost = async (req, res, next) => {
  try {
    // const exists = await Post.deleteOne({ _id: req.params.id });
    const exists = await Post.updateOne(
      { _id: req.params.id },
      { isDeleted: true }
    );
    const updatePost = await Post.findById(req.params.id);
    if (exists == null) return sendError(res, 400, "post does not exist");
    else {
      console.log("post deleted!");

      res.status(200).send({
        status: "OK",
        post: updatePost,
      });
    }
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

/* ********************************************************** */

// const getMyPosts = async (req, res, next) => {
//   // User.find({ id: req.user.id }, function (err, docs) {
//   //     if (err) {
//   //         console.log(err);
//   //     }
//   //     else {
//   //         Post.find({ sender: docs }, function (err, docs_post) {
//   //             if (err) {
//   //                 console.log(err);
//   //             }
//   //             else {
//   //                 res.status(200).send(docs_post);
//   //             }
//   //         });
//   //     }
//   // });
// };

const getMyPosts = async (req, res, next) => {
  var sender = req.params.id;
  console.log(sender);
  Post.find({ sender: sender }, function (err, docs) {
    if (err) console.log(err);
    else res.status(200).send(docs);
  });
};

const allPostsFiltered = async (req, res, next) => {
  //   var post = await Post.findById(req.query.id);
  //   var sender = post.sender;
  //   var circle = post.friends_circle;
  //   console.log("sender = " + sender);
  //   console.log("circle444444444444 = " + circle);
  //   // var list = UserController.getFriendsList(sender,circle);
  //   console.log("list = " + list);
};

module.exports = {
  getAllPosts,
  // getQuestions,
  // getSOSs,
  getPostsById,
  addPosts,
  getMyPosts,
  editPost,
  deletePost,
  allPostsFiltered,
};

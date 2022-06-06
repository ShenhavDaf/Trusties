const Post = require("../Models/post_model");
const Sos = require("../Models/sos_model");
const User = require("../Models/user_model");
const Category = require("../Models/category_model");

const addSos = async (req, res, next) => {
  var email = req.body.email;
  const user = await User.findOne({ email: email });
  var description = req.body.description;
  var title = req.body.title;
  var time = req.body.time;
  var role = req.body.role;
  var category = req.body.category;
  var friendsCircle = Number(req.body.circle);
  var photo = req.body.photo;
  var location = req.body.location;
  var address = req.body.address;

  const findCategory = await Category.findOne({ name: category });

  const sos = await Sos({
    sender: user,
    title: title,
    time: time,
    description: description,
    role: role,
    friends_circle: friendsCircle,
    category: category,
    volunteers: [],
    address: address,
    approved_volunteer: null,
    photo: photo,
    location: location,
  });
  findCategory.save(async (error) => {
    if (error) {
      res.status(400).send({
        status: "fail",
        error: error.message,
      });
    } else {
      await Category.updateOne(
        { name: category },
        {
          $push: { posts: [sos._id] },
        }
      );
    }
  });

  sos.save((error, newPost) => {
    if (error) {
      res.status(400).send({
        status: "fail",
        error: error.message,
      });
    } else {
      res.status(200).send({
        status: "OK",
        _id: sos._id,
      });
    }
  });
};

// const getSoss = async (req, res, next) => {
//   Post.find({ role: "SOS" }, function (err, docs) {
//     if (err) {
//       console.log(err);
//     } else {
//       res.status(200).send(docs);
//     }
//   });
// };

//# PARAMS
// req.params.id -sos
const getSosVolunteers = async (req, res, next) => {
  Post.findById(req.query.id)
    .populate({
      path: "volunteers",
      model: "User",
    })
    .exec((err, postInclude) => {
      res.status(200).send(postInclude.volunteers);
    });
};

//# PARAMS
//req.body.vol_id - user
// req.params.id -sos
const volunteer = async (req, res, next) => {
  const user = await User.findById(req.body.vol_id);

  Post.findById(req.params.id)
    .where({ role: "SOS" })
    .populate({ path: "volunteers" })
    .exec((err, sos) => {
      if (err) {
        res.status(400).send({
          status: "fail",
          error: error.message,
          message: "SOS not found in DB",
        });
      }
      if (sos.status == "OPEN") {
        sos.volunteers.addToSet(user._id);
        sos.save(function (err) {
          if (err) {
            res.status(400).send({
              status: "fail",
              error: err.message,
              message: "the request to help NOT saved.",
            });
          }
        });
      }
    });
};

//# PARAMS
//req.body.vol_id - user
// req.params.id -sos
const cancelVolunteer = async (req, res, next) => {
  const user = await User.findById(req.body.user_request_id);

  Post.findById(req.params.id)
    .where({ role: "SOS" })
    .populate({ path: "volunteers" })
    .exec((err, sos) => {
      if (err) {
        res.status(400).send({
          status: "fail",
          error: error.message,
          message: "SOS not found in DB",
        });
      }

      for (var i = 0; i < sos.volunteers.length; i++) {
        if (sos.volunteers[i].email == user.email) {
          sos.volunteers.splice(i, 1);
        }
      }
      sos.save(function (err) {
        if (err) {
          res.status(400).send({
            status: "fail",
            error: err.message,
            message: "the request to an help NOT saved.",
          });
        }
      });
    });
};

//# PARAMS
// req.params.id -sos
const closeSos = async (req, res, next) => {
  Post.findById(req.params.id)
    .where({ role: "SOS" })
    .populate({ path: "volunteers" })
    .exec((err, sos) => {
      if (err) {
        res.status(400).send({
          status: "fail",
          error: error.message,
          message: "SOS not found in DB",
        });
      }

      sos.status = "CLOSE";
      var size = sos.volunteers.length;
      sos.volunteers.splice(0, size);

      sos.save(function (err) {
        if (err) {
          res.status(400).send({
            status: "fail",
            error: err.message,
            message: "the request to help NOT saved.",
          });
        }
      });
    });
};

//# PARAMS
//req.body.vol_id - user
// req.params.id -sos
const approveVolunteer = async (req, res, next) => {
  const user = await User.findById(req.body.vol_id);

  Post.findById(req.params.id)
    .where({ role: "SOS" })
    .populate({
      path: "volunteers",
      model: "User",
    })
    .populate({
      path: "approved_volunteer",
      model: "User",
    })
    .exec((err, sos) => {
      if (err) {
        res.status(400).send({
          status: "fail",
          error: error.message,
          message: "SOS not found in DB",
        });
      }

      for (var i = 0; i < sos.volunteers.length; i++) {
        if (sos.volunteers[i].email == user.email) {
          sos.volunteers.splice(i, 1);
        }
      }
      sos.approved_volunteer = user._id;
      sos.status = "WAITING";

      sos.save(function (err) {
        if (err) {
          res.status(400).send({
            status: "fail",
            error: err.message,
            message: "the request to help NOT saved.",
          });
        }
      });
    });
};

//# PARAMS
//req.body.vol_id - user
// req.params.id -sos
const cancelApproveVolunteer = async (req, res, next) => {
  //if the post publisher want to cancel the approve OR the request sender wants to cancel the request after approve

  const user = await User.findById(req.body.request_id);

  Post.findById(req.params.id)
    .where({ role: "SOS" })
    .populate({ path: "volunteers" })
    .populate({ path: "approved_volunteer" })
    .exec((err, sos) => {
      if (err) {
        res.status(400).send({
          status: "fail",
          error: error.message,
          message: "SOS not found in DB",
        });
      }

      sos.status = "OPEN";
      sos.approved_help = null;

      sos.save(function (err) {
        if (err) {
          res.status(400).send({
            status: "fail",
            error: err.message,
            message: "the request to cancelApproveRequest NOT saved.",
          });
        }
      });
    });
};

//# PARAMS
// req.params.id -sos
const getApprovedVolunteer = async (req, res, next) => {
  //if the post publisher want to cancel the approve OR the request sender wants to cancel the request after approve

  Post.findById(req.params.id)
    .where({ role: "SOS" })
    .populate({ path: "approved_volunteer", model: User })
    .exec((err, sos) => {
      if (err) {
        res.status(400).send({
          status: "fail",
          error: error.message,
          message: "SOS not found in DB",
        });
      } else {
        res.status(200).send(sos.approved_volunteer);
      }
    });
};

module.exports = {
  addSos,
  // getSoss,
  volunteer,
  approveVolunteer,
  cancelVolunteer,
  cancelApproveVolunteer,
  getSosVolunteers,
  closeSos,
  getApprovedVolunteer,
};

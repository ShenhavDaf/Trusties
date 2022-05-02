const { reject } = require("bcrypt/promises");
const User = require("../Models/user_model");

// const getFriendsList2 = async (req, res) => {
//   try {
//     const circle = req.query.circle;
//     const user = await User.findById(req.query.id);
//     const firstList = user.friends;
//     const secondList = new Array();
//     let thirdList = new Array();

//     let l;

//     if (circle == 2 || circle == 3) {
//       firstList.forEach(async (first_id) => {
//         // enter the first circle to second
//         secondList.push(first_id);

//         const friend = await User.findById(first_id);
//         friend.friends.forEach(async (second_id) => {
//           // if second_id is not in the firstList and does not enter in the future
//           // and not post author
//           if (firstList.indexOf(second_id) == -1 && second_id != req.query.id) {
//             secondList.push(second_id);
//           }
//         });

//         if (circle == 2) {
//           // console.log("2 --> " + secondList);
//           // res.status(200).send(secondList);
//           l = secondList;
//         } else {
//           thirdList = await findCircle3(secondList);
//           if (thirdList != null)
//             //  res.status(200).send(thirdList);
//             l = thirdList;
//         }
//       });
//     }
//     // circle == 1
//     else {
//       // console.log("1 --> " + firstList);
//       // res.status(200).send(firstList);
//       l = firstList;
//     }

//     if (l != null) {
//       console.log("l " + l);
//       res.status(200).send(l);
//     }
//     /*

//     */
//   } catch (err) {
//     console.log("----" + err.message);

//     res.status(400).send({
//       status: "fail",
//       error: err.message,
//     });
//   }
// };

/***************************** */
/***************************** */
/***************************** */
/***************************** */
/***************************** */
/***************************** */

const getFriendsList = async (req, res) => {
  try {
    const circle = req.query.circle;
    const user = await User.findById(req.query.id);
    const firstList = user.friends;
    const secondList = new Array();
    let thirdList = new Array();
    let list;

    var foo = new Promise((resolve, reject) => {
      if (circle == 1) {
        list = firstList;
        resolve(list);
      } else if (circle == 2 || circle == 3) {
        firstList.forEach(async (id_sec) => {
          secondList.push(id_sec);
          const friend = await User.findById(id_sec);

          var sec = new Promise((resolve, reject) => {
            friend.friends.forEach((id) => {
              secondList.push(id);
              console.log("2==> " + secondList);
            });

            resolve(secondList);
          });

          sec.then(() => {
            if (circle == 2) {
              list = secondList;
              //to final
              resolve(list);
            } else if (circle == 3) {
              // secondList.forEach(async (id) => {
              //   thirdList.push(id);
              //   const friend = await User.findById(id);

              //   new Promise(function (resolve, reject) {
              //     friend.friends.forEach((third_id) => {
              //       console.log(third_id);
              //       thirdList.push(third_id);
              //       console.log("");
              //       console.log(")))))))))) " + thirdList);
              //     });
              //     resolve(thirdList);
              //   }).then(function (data) {
              //     console.log("");
              //     console.log("data = " + data);
              //     list = data;
              //   });
              // });

              thirdList.push("shenhav");
              list = thirdList;
              //to final
              resolve(list);
            }
          });
        });
      }
    });

    foo.then(() => {
      console.log("final = " + list);
      res.status(200).send(list);
    });

    /**
     *
     */
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

module.exports = {
  // findUserByEmail,
  // findUserById,
  // editUser,
  getFriendsList,
};

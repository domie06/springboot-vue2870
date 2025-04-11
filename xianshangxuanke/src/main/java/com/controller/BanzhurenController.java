
package com.controller;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import com.alibaba.fastjson.JSONObject;
import java.util.*;
import org.springframework.beans.BeanUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.ContextLoader;
import javax.servlet.ServletContext;
import com.service.TokenService;
import com.utils.*;
import java.lang.reflect.InvocationTargetException;

import com.service.DictionaryService;
import org.apache.commons.lang3.StringUtils;
import com.annotation.IgnoreAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.*;
import com.entity.view.*;
import com.service.*;
import com.utils.PageUtils;
import com.utils.R;
import com.alibaba.fastjson.*;

/**
 * 班主任
 * 后端接口
 * @author
 * @email
*/
@RestController
@Controller
@RequestMapping("/banzhuren")
public class BanzhurenController {
    private static final Logger logger = LoggerFactory.getLogger(BanzhurenController.class);

    @Autowired
    private BanzhurenService banzhurenService;


    @Autowired
    private TokenService tokenService;
    @Autowired
    private DictionaryService dictionaryService;

    //级联表service

    @Autowired
    private XueshengService xueshengService;


    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("page方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永不会进入");
        else if("学生".equals(role))
            params.put("xueshengId",request.getSession().getAttribute("userId"));
        else if("班主任".equals(role))
            params.put("banzhurenId",request.getSession().getAttribute("userId"));
        if(params.get("orderBy")==null || params.get("orderBy")==""){
            params.put("orderBy","id");
        }
        PageUtils page = banzhurenService.queryPage(params);

        //字典表数据转换
        List<BanzhurenView> list =(List<BanzhurenView>)page.getList();
        for(BanzhurenView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c, request);
        }
        return R.ok().put("data", page);
    }

    /**
    * 后端详情
    */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("info方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        BanzhurenEntity banzhuren = banzhurenService.selectById(id);
        if(banzhuren !=null){
            //entity转view
            BanzhurenView view = new BanzhurenView();
            BeanUtils.copyProperties( banzhuren , view );//把实体数据重构到view中

            //修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }

    }

    /**
    * 后端保存
    */
    @RequestMapping("/save")
    public R save(@RequestBody BanzhurenEntity banzhuren, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,banzhuren:{}",this.getClass().getName(),banzhuren.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永远不会进入");

        Wrapper<BanzhurenEntity> queryWrapper = new EntityWrapper<BanzhurenEntity>()
            .eq("username", banzhuren.getUsername())
            .or()
            .eq("banzhuren_phone", banzhuren.getBanzhurenPhone())
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        BanzhurenEntity banzhurenEntity = banzhurenService.selectOne(queryWrapper);
        if(banzhurenEntity==null){
            banzhuren.setCreateTime(new Date());
            banzhuren.setPassword("123456");
            banzhurenService.insert(banzhuren);
            return R.ok();
        }else {
            return R.error(511,"账户或者班主任手机号已经被使用");
        }
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody BanzhurenEntity banzhuren, HttpServletRequest request){
        logger.debug("update方法:,,Controller:{},,banzhuren:{}",this.getClass().getName(),banzhuren.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
//        if(false)
//            return R.error(511,"永远不会进入");
        //根据字段查询是否有相同数据
        Wrapper<BanzhurenEntity> queryWrapper = new EntityWrapper<BanzhurenEntity>()
            .notIn("id",banzhuren.getId())
            .andNew()
            .eq("username", banzhuren.getUsername())
            .or()
            .eq("banzhuren_phone", banzhuren.getBanzhurenPhone())
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        BanzhurenEntity banzhurenEntity = banzhurenService.selectOne(queryWrapper);
        if("".equals(banzhuren.getBanzhurenPhoto()) || "null".equals(banzhuren.getBanzhurenPhoto())){
                banzhuren.setBanzhurenPhoto(null);
        }
        if(banzhurenEntity==null){
            banzhurenService.updateById(banzhuren);//根据id更新
            return R.ok();
        }else {
            return R.error(511,"账户或者班主任手机号已经被使用");
        }
    }

    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
        logger.debug("delete:,,Controller:{},,ids:{}",this.getClass().getName(),ids.toString());
        banzhurenService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }


    /**
     * 批量上传
     */
    @RequestMapping("/batchInsert")
    public R save( String fileName, HttpServletRequest request){
        logger.debug("batchInsert方法:,,Controller:{},,fileName:{}",this.getClass().getName(),fileName);
        Integer yonghuId = Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId")));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            List<BanzhurenEntity> banzhurenList = new ArrayList<>();//上传的东西
            Map<String, List<String>> seachFields= new HashMap<>();//要查询的字段
            Date date = new Date();
            int lastIndexOf = fileName.lastIndexOf(".");
            if(lastIndexOf == -1){
                return R.error(511,"该文件没有后缀");
            }else{
                String suffix = fileName.substring(lastIndexOf);
                if(!".xls".equals(suffix)){
                    return R.error(511,"只支持后缀为xls的excel文件");
                }else{
                    URL resource = this.getClass().getClassLoader().getResource("../../upload/" + fileName);//获取文件路径
                    File file = new File(resource.getFile());
                    if(!file.exists()){
                        return R.error(511,"找不到上传文件，请联系管理员");
                    }else{
                        List<List<String>> dataList = PoiUtil.poiImport(file.getPath());//读取xls文件
                        dataList.remove(0);//删除第一行，因为第一行是提示
                        for(List<String> data:dataList){
                            //循环
                            BanzhurenEntity banzhurenEntity = new BanzhurenEntity();
//                            banzhurenEntity.setUsername(data.get(0));                    //账户 要改的
//                            //banzhurenEntity.setPassword("123456");//密码
//                            banzhurenEntity.setBanzhurenName(data.get(0));                    //班主任姓名 要改的
//                            banzhurenEntity.setBanzhurenPhone(data.get(0));                    //班主任手机号 要改的
//                            banzhurenEntity.setBanzhurenPhoto("");//详情和图片
//                            banzhurenEntity.setSexTypes(Integer.valueOf(data.get(0)));   //性别 要改的
//                            banzhurenEntity.setBanzhurenEmail(data.get(0));                    //电子邮箱 要改的
//                            banzhurenEntity.setCreateTime(date);//时间
                            banzhurenList.add(banzhurenEntity);


                            //把要查询是否重复的字段放入map中
                                //账户
                                if(seachFields.containsKey("username")){
                                    List<String> username = seachFields.get("username");
                                    username.add(data.get(0));//要改的
                                }else{
                                    List<String> username = new ArrayList<>();
                                    username.add(data.get(0));//要改的
                                    seachFields.put("username",username);
                                }
                                //班主任手机号
                                if(seachFields.containsKey("banzhurenPhone")){
                                    List<String> banzhurenPhone = seachFields.get("banzhurenPhone");
                                    banzhurenPhone.add(data.get(0));//要改的
                                }else{
                                    List<String> banzhurenPhone = new ArrayList<>();
                                    banzhurenPhone.add(data.get(0));//要改的
                                    seachFields.put("banzhurenPhone",banzhurenPhone);
                                }
                        }

                        //查询是否重复
                         //账户
                        List<BanzhurenEntity> banzhurenEntities_username = banzhurenService.selectList(new EntityWrapper<BanzhurenEntity>().in("username", seachFields.get("username")));
                        if(banzhurenEntities_username.size() >0 ){
                            ArrayList<String> repeatFields = new ArrayList<>();
                            for(BanzhurenEntity s:banzhurenEntities_username){
                                repeatFields.add(s.getUsername());
                            }
                            return R.error(511,"数据库的该表中的 [账户] 字段已经存在 存在数据为:"+repeatFields.toString());
                        }
                         //班主任手机号
                        List<BanzhurenEntity> banzhurenEntities_banzhurenPhone = banzhurenService.selectList(new EntityWrapper<BanzhurenEntity>().in("banzhuren_phone", seachFields.get("banzhurenPhone")));
                        if(banzhurenEntities_banzhurenPhone.size() >0 ){
                            ArrayList<String> repeatFields = new ArrayList<>();
                            for(BanzhurenEntity s:banzhurenEntities_banzhurenPhone){
                                repeatFields.add(s.getBanzhurenPhone());
                            }
                            return R.error(511,"数据库的该表中的 [班主任手机号] 字段已经存在 存在数据为:"+repeatFields.toString());
                        }
                        banzhurenService.insertBatch(banzhurenList);
                        return R.ok();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return R.error(511,"批量插入数据异常，请联系管理员");
        }
    }


    /**
    * 登录
    */
    @IgnoreAuth
    @RequestMapping(value = "/login")
    public R login(String username, String password, String captcha, HttpServletRequest request) {
        BanzhurenEntity banzhuren = banzhurenService.selectOne(new EntityWrapper<BanzhurenEntity>().eq("username", username));
        if(banzhuren==null || !banzhuren.getPassword().equals(password))
            return R.error("账号或密码不正确");
        //  // 获取监听器中的字典表
        // ServletContext servletContext = ContextLoader.getCurrentWebApplicationContext().getServletContext();
        // Map<String, Map<Integer, String>> dictionaryMap= (Map<String, Map<Integer, String>>) servletContext.getAttribute("dictionaryMap");
        // Map<Integer, String> role_types = dictionaryMap.get("role_types");
        // role_types.get(.getRoleTypes());
        String token = tokenService.generateToken(banzhuren.getId(),username, "banzhuren", "班主任");
        R r = R.ok();
        r.put("token", token);
        r.put("role","班主任");
        r.put("username",banzhuren.getBanzhurenName());
        r.put("tableName","banzhuren");
        r.put("userId",banzhuren.getId());
        return r;
    }

    /**
    * 注册
    */
    @IgnoreAuth
    @PostMapping(value = "/register")
    public R register(@RequestBody BanzhurenEntity banzhuren){
//    	ValidatorUtils.validateEntity(user);
        Wrapper<BanzhurenEntity> queryWrapper = new EntityWrapper<BanzhurenEntity>()
            .eq("username", banzhuren.getUsername())
            .or()
            .eq("banzhuren_phone", banzhuren.getBanzhurenPhone())
            ;
        BanzhurenEntity banzhurenEntity = banzhurenService.selectOne(queryWrapper);
        if(banzhurenEntity != null)
            return R.error("账户或者班主任手机号已经被使用");
        banzhuren.setCreateTime(new Date());
        banzhurenService.insert(banzhuren);
        return R.ok();
    }

    /**
     * 重置密码
     */
    @GetMapping(value = "/resetPassword")
    public R resetPassword(Integer  id){
        BanzhurenEntity banzhuren = new BanzhurenEntity();
        banzhuren.setPassword("123456");
        banzhuren.setId(id);
        banzhurenService.updateById(banzhuren);
        return R.ok();
    }


    /**
     * 忘记密码
     */
    @IgnoreAuth
    @RequestMapping(value = "/resetPass")
    public R resetPass(String username, HttpServletRequest request) {
        BanzhurenEntity banzhuren = banzhurenService.selectOne(new EntityWrapper<BanzhurenEntity>().eq("username", username));
        if(banzhuren!=null){
            banzhuren.setPassword("123456");
            boolean b = banzhurenService.updateById(banzhuren);
            if(!b){
               return R.error();
            }
        }else{
           return R.error("账号不存在");
        }
        return R.ok();
    }


    /**
    * 获取用户的session用户信息
    */
    @RequestMapping("/session")
    public R getCurrBanzhuren(HttpServletRequest request){
        Integer id = (Integer)request.getSession().getAttribute("userId");
        BanzhurenEntity banzhuren = banzhurenService.selectById(id);
        if(banzhuren !=null){
            //entity转view
            BanzhurenView view = new BanzhurenView();
            BeanUtils.copyProperties( banzhuren , view );//把实体数据重构到view中

            //修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }
    }


    /**
    * 退出
    */
    @GetMapping(value = "logout")
    public R logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return R.ok("退出成功");
    }




    /**
    * 前端列表
    */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("list方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));

        // 没有指定排序字段就默认id倒序
        if(StringUtil.isEmpty(String.valueOf(params.get("orderBy")))){
            params.put("orderBy","id");
        }
        PageUtils page = banzhurenService.queryPage(params);

        //字典表数据转换
        List<BanzhurenView> list =(List<BanzhurenView>)page.getList();
        for(BanzhurenView c:list)
            dictionaryService.dictionaryConvert(c, request); //修改对应字典表字段
        return R.ok().put("data", page);
    }

    /**
    * 前端详情
    */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("detail方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        BanzhurenEntity banzhuren = banzhurenService.selectById(id);
            if(banzhuren !=null){


                //entity转view
                BanzhurenView view = new BanzhurenView();
                BeanUtils.copyProperties( banzhuren , view );//把实体数据重构到view中

                //修改对应字典表字段
                dictionaryService.dictionaryConvert(view, request);
                return R.ok().put("data", view);
            }else {
                return R.error(511,"查不到数据");
            }
    }


    /**
    * 前端保存
    */
    @RequestMapping("/add")
    public R add(@RequestBody BanzhurenEntity banzhuren, HttpServletRequest request){
        logger.debug("add方法:,,Controller:{},,banzhuren:{}",this.getClass().getName(),banzhuren.toString());
        Wrapper<BanzhurenEntity> queryWrapper = new EntityWrapper<BanzhurenEntity>()
            .eq("username", banzhuren.getUsername())
            .or()
            .eq("banzhuren_phone", banzhuren.getBanzhurenPhone())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        BanzhurenEntity banzhurenEntity = banzhurenService.selectOne(queryWrapper);
        if(banzhurenEntity==null){
            banzhuren.setCreateTime(new Date());
        banzhuren.setPassword("123456");
        banzhurenService.insert(banzhuren);
            return R.ok();
        }else {
            return R.error(511,"账户或者班主任手机号已经被使用");
        }
    }


}
